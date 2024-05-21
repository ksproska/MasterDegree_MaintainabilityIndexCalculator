public class Example {
    public void deploy(final DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        final DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        final EjbJarMetaData metaData = deploymentUnit.getAttachment(EjbDeploymentAttachmentKeys.EJB_JAR_METADATA);
        final EEModuleDescription eeModuleDescription = deploymentUnit.getAttachment(Attachments.EE_MODULE_DESCRIPTION);
        final Module module = deploymentUnit.getAttachment(org.jboss.as.server.deployment.Attachments.MODULE);
        final DeploymentReflectionIndex index = deploymentUnit.getAttachment(org.jboss.as.server.deployment.Attachments.REFLECTION_INDEX);
        if (metaData == null) {
            return;
        }
        if (metaData.getAssemblyDescriptor() == null) {
            return;
        }
        if (metaData.getAssemblyDescriptor().getInterceptorBindings() == null) {
            return;
        }
        final Set<String> interceptorClasses = new HashSet<String>();
        if (metaData.getInterceptors() != null) {
            for (final InterceptorMetaData interceptor : metaData.getInterceptors()) {
                interceptorClasses.add(interceptor.getInterceptorClass());
            }
        }
        final Map<String, List<InterceptorBindingMetaData>> bindingsPerComponent = new HashMap<String, List<InterceptorBindingMetaData>>();
        final List<InterceptorBindingMetaData> defaultInterceptorBindings = new ArrayList<InterceptorBindingMetaData>();
        for (final InterceptorBindingMetaData binding : metaData.getAssemblyDescriptor().getInterceptorBindings()) {
            if (binding.getEjbName().equals("*")) {
                if (binding.getMethod() != null) {
                    throw EjbLogger.ROOT_LOGGER.defaultInterceptorsNotBindToMethod();
                }
                if (binding.getInterceptorOrder() != null) {
                    throw EjbLogger.ROOT_LOGGER.defaultInterceptorsNotSpecifyOrder();
                }
                defaultInterceptorBindings.add(binding);
            } else if (ejbNameRegexService.isEjbNameRegexAllowed()) {
                Pattern pattern = Pattern.compile(binding.getEjbName());
                for (final ComponentDescription componentDescription : eeModuleDescription.getComponentDescriptions()) {
                    if (componentDescription instanceof EJBComponentDescription) {
                        String ejbName = ((EJBComponentDescription) componentDescription).getEJBName();
                        if (pattern.matcher(ejbName).matches()) {
                            List<InterceptorBindingMetaData> bindings = bindingsPerComponent.get(ejbName);
                            if (bindings == null) {
                                bindingsPerComponent.put(ejbName, bindings = new ArrayList<InterceptorBindingMetaData>());
                            }
                            bindings.add(binding);
                        }
                    }
                }
            } else {
                List<InterceptorBindingMetaData> bindings = bindingsPerComponent.get(binding.getEjbName());
                if (bindings == null) {
                    bindingsPerComponent.put(binding.getEjbName(), bindings = new ArrayList<InterceptorBindingMetaData>());
                }
                bindings.add(binding);
            }
        }
        final List<InterceptorDescription> defaultInterceptors = new ArrayList<InterceptorDescription>();
        for (InterceptorBindingMetaData binding : defaultInterceptorBindings) {
            if (binding.getInterceptorClasses() != null) {
                for (final String clazz : binding.getInterceptorClasses()) {
                    if (interceptorClasses.contains(clazz)) {
                        defaultInterceptors.add(new InterceptorDescription(clazz));
                    } else {
                        ROOT_LOGGER.defaultInterceptorClassNotListed(clazz);
                    }
                }
            }
        }
        for (final ComponentDescription componentDescription : eeModuleDescription.getComponentDescriptions()) {
            final Class<?> componentClass;
            try {
                componentClass = module.getClassLoader().loadClass(componentDescription.getComponentClassName());
            } catch (ClassNotFoundException e) {
                throw EjbLogger.ROOT_LOGGER.failToLoadComponentClass(e, componentDescription.getComponentClassName());
            }
            final List<InterceptorBindingMetaData> bindings = bindingsPerComponent.get(componentDescription.getComponentName());
            final Map<Method, List<InterceptorBindingMetaData>> methodInterceptors = new HashMap<Method, List<InterceptorBindingMetaData>>();
            final List<InterceptorBindingMetaData> classLevelBindings = new ArrayList<InterceptorBindingMetaData>();
            boolean classLevelExcludeDefaultInterceptors = false;
            Map<Method, Boolean> methodLevelExcludeDefaultInterceptors = new HashMap<Method, Boolean>();
            Map<Method, Boolean> methodLevelExcludeClassInterceptors = new HashMap<Method, Boolean>();
            boolean classLevelAbsoluteOrder = false;
            final Map<Method, Boolean> methodLevelAbsoluteOrder = new HashMap<Method, Boolean>();
            if (bindings != null) {
                for (final InterceptorBindingMetaData binding : bindings) {
                    if (binding.getMethod() == null) {
                        classLevelBindings.add(binding);
                        if (binding.isExcludeDefaultInterceptors()) {
                            classLevelExcludeDefaultInterceptors = true;
                        }
                        if (binding.isTotalOrdering()) {
                            if (classLevelAbsoluteOrder) {
                                throw EjbLogger.ROOT_LOGGER.twoEjbBindingsSpecifyAbsoluteOrder(componentClass.toString());
                            } else {
                                classLevelAbsoluteOrder = true;
                            }
                        }
                    } else {
                        final NamedMethodMetaData methodData = binding.getMethod();
                        final ClassReflectionIndex classIndex = index.getClassIndex(componentClass);
                        Method resolvedMethod = null;
                        if (methodData.getMethodParams() == null) {
                            final Collection<Method> methods = classIndex.getAllMethods(methodData.getMethodName());
                            if (methods.isEmpty()) {
                                throw EjbLogger.ROOT_LOGGER.failToFindMethodInEjbJarXml(componentClass.getName(), methodData.getMethodName());
                            } else if (methods.size() > 1) {
                                throw EjbLogger.ROOT_LOGGER.multipleMethodReferencedInEjbJarXml(methodData.getMethodName(), componentClass.getName());
                            }
                            resolvedMethod = methods.iterator().next();
                        } else {
                            final Collection<Method> methods = classIndex.getAllMethods(methodData.getMethodName(), methodData.getMethodParams().size());
                            for (final Method method : methods) {
                                boolean match = true;
                                for (int i = 0; i < method.getParameterCount(); ++i) {
                                    if (!method.getParameterTypes()[i].getName().equals(methodData.getMethodParams().get(i))) {
                                        match = false;
                                        break;
                                    }
                                }
                                if (match) {
                                    resolvedMethod = method;
                                    break;
                                }
                            }
                            if (resolvedMethod == null) {
                                throw EjbLogger.ROOT_LOGGER.failToFindMethodWithParameterTypes(componentClass.getName(), methodData.getMethodName(), methodData.getMethodParams());
                            }
                        }
                        List<InterceptorBindingMetaData> list = methodInterceptors.get(resolvedMethod);
                        if (list == null) {
                            methodInterceptors.put(resolvedMethod, list = new ArrayList<InterceptorBindingMetaData>());
                        }
                        list.add(binding);
                        if (binding.isExcludeDefaultInterceptors()) {
                            methodLevelExcludeDefaultInterceptors.put(resolvedMethod, true);
                        }
                        if (binding.isExcludeClassInterceptors()) {
                            methodLevelExcludeClassInterceptors.put(resolvedMethod, true);
                        }
                        if (binding.isTotalOrdering()) {
                            if (methodLevelAbsoluteOrder.containsKey(resolvedMethod)) {
                                throw EjbLogger.ROOT_LOGGER.twoEjbBindingsSpecifyAbsoluteOrder(resolvedMethod.toString());
                            } else {
                                methodLevelAbsoluteOrder.put(resolvedMethod, true);
                            }
                        }
                    }
                }
            }
            componentDescription.setDefaultInterceptors(defaultInterceptors);
            if (classLevelExcludeDefaultInterceptors) {
                componentDescription.setExcludeDefaultInterceptors(true);
            }
            final List<InterceptorDescription> classLevelInterceptors = new ArrayList<InterceptorDescription>();
            if (classLevelAbsoluteOrder) {
                for (final InterceptorBindingMetaData binding : classLevelBindings) {
                    if (binding.isTotalOrdering()) {
                        for (final String interceptor : binding.getInterceptorOrder()) {
                            classLevelInterceptors.add(new InterceptorDescription(interceptor));
                        }
                    }
                }
                componentDescription.setExcludeDefaultInterceptors(true);
            } else {
                classLevelInterceptors.addAll(componentDescription.getClassInterceptors());
                for (InterceptorBindingMetaData binding : classLevelBindings) {
                    if (binding.getInterceptorClasses() != null) {
                        for (final String interceptor : binding.getInterceptorClasses()) {
                            classLevelInterceptors.add(new InterceptorDescription(interceptor));
                        }
                    }
                }
            }
            componentDescription.setClassInterceptors(classLevelInterceptors);
            for (Map.Entry<Method, List<InterceptorBindingMetaData>> entry : methodInterceptors.entrySet()) {
                final Method method = entry.getKey();
                final List<InterceptorBindingMetaData> methodBindings = entry.getValue();
                boolean totalOrder = methodLevelAbsoluteOrder.containsKey(method);
                final MethodIdentifier methodIdentifier = MethodIdentifier.getIdentifierForMethod(method);
                Boolean excludeDefaultInterceptors = methodLevelExcludeDefaultInterceptors.get(method);
                excludeDefaultInterceptors = excludeDefaultInterceptors == null ? Boolean.FALSE : excludeDefaultInterceptors;
                if (!excludeDefaultInterceptors) {
                    excludeDefaultInterceptors = componentDescription.isExcludeDefaultInterceptors() || componentDescription.isExcludeDefaultInterceptors(methodIdentifier);
                }
                Boolean excludeClassInterceptors = methodLevelExcludeClassInterceptors.get(method);
                excludeClassInterceptors = excludeClassInterceptors == null ? Boolean.FALSE : excludeClassInterceptors;
                if (!excludeClassInterceptors) {
                    excludeClassInterceptors = componentDescription.isExcludeClassInterceptors(methodIdentifier);
                }
                final List<InterceptorDescription> methodLevelInterceptors = new ArrayList<InterceptorDescription>();
                if (totalOrder) {
                    for (final InterceptorBindingMetaData binding : methodBindings) {
                        if (binding.isTotalOrdering()) {
                            for (final String interceptor : binding.getInterceptorOrder()) {
                                methodLevelInterceptors.add(new InterceptorDescription(interceptor));
                            }
                        }
                    }
                } else {
                    if (!excludeDefaultInterceptors) {
                        methodLevelInterceptors.addAll(defaultInterceptors);
                    }
                    if (!excludeClassInterceptors) {
                        for (InterceptorDescription interceptor : classLevelInterceptors) {
                            methodLevelInterceptors.add(interceptor);
                        }
                    }
                    List<InterceptorDescription> annotationMethodLevel = componentDescription.getMethodInterceptors().get(methodIdentifier);
                    if (annotationMethodLevel != null) {
                        methodLevelInterceptors.addAll(annotationMethodLevel);
                    }
                    for (InterceptorBindingMetaData binding : methodBindings) {
                        if (binding.getInterceptorClasses() != null) {
                            for (final String interceptor : binding.getInterceptorClasses()) {
                                methodLevelInterceptors.add(new InterceptorDescription(interceptor));
                            }
                        }
                    }
                }
                componentDescription.excludeClassInterceptors(methodIdentifier);
                componentDescription.excludeDefaultInterceptors(methodIdentifier);
                componentDescription.setMethodInterceptors(methodIdentifier, methodLevelInterceptors);
            }
        }
    }
}
