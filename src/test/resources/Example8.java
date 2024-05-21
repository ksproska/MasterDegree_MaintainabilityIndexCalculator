public class Example {
    private static Map<String, RoleDescriptor> initializeReservedRoles() {
        return Map.ofEntries(entry("superuser", SUPERUSER_ROLE_DESCRIPTOR), entry("transport_client", new RoleDescriptor("transport_client", new String[]{"transport_client"}, null, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants the privileges required to access the cluster through the Java Transport Client. " + "The Java Transport Client fetches information about the nodes in the cluster using " + "the Node Liveness API and the Cluster State API (when sniffing is enabled). " + "Assign your users this role if they use the Transport Client.")), entry("kibana_admin", kibanaAdminUser("kibana_admin", MetadataUtils.DEFAULT_RESERVED_METADATA)), entry("kibana_user", kibanaAdminUser("kibana_user", MetadataUtils.getDeprecatedReservedMetadata("Please use the [kibana_admin] role instead"))), entry("monitoring_user", new RoleDescriptor("monitoring_user", new String[]{"cluster:monitor/main", "cluster:monitor/xpack/info", TransportRemoteInfoAction.TYPE.name()}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices(".monitoring-*").privileges("read", "read_cross_cluster").build(), RoleDescriptor.IndicesPrivileges.builder().indices("/metrics-(beats|elasticsearch|enterprisesearch|kibana|logstash).*/").privileges("read", "read_cross_cluster").build(), RoleDescriptor.IndicesPrivileges.builder().indices("metricbeat-*").privileges("read", "read_cross_cluster").build()}, new RoleDescriptor.ApplicationResourcePrivileges[]{RoleDescriptor.ApplicationResourcePrivileges.builder().application("kibana-*").resources("*").privileges("reserved_monitoring").build()}, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, new RoleDescriptor.RemoteIndicesPrivileges[]{getRemoteIndicesReadPrivileges(".monitoring-*"), getRemoteIndicesReadPrivileges("/metrics-(beats|elasticsearch|enterprisesearch|kibana|logstash).*/"), getRemoteIndicesReadPrivileges("metricbeat-*")}, null, null, "Grants the minimum privileges required for any user of X-Pack monitoring other than those required to use Kibana. " + "This role grants access to the monitoring indices and grants privileges necessary " + "for reading basic cluster information. " + "This role also includes all Kibana privileges for the Elastic Stack monitoring features. " + "Monitoring users should also be assigned the kibana_admin role, " + "or another role with access to the Kibana instance.")), entry("remote_monitoring_agent", new RoleDescriptor("remote_monitoring_agent", new String[]{"manage_index_templates", "manage_ingest_pipelines", "monitor", GetLifecycleAction.NAME, ILMActions.PUT.name(), "cluster:monitor/xpack/watcher/watch/get", "cluster:admin/xpack/watcher/watch/put", "cluster:admin/xpack/watcher/watch/delete"}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices(".monitoring-*").privileges("all").build(), RoleDescriptor.IndicesPrivileges.builder().indices("metricbeat-*").privileges("index", "create_index", "view_index_metadata", TransportIndicesAliasesAction.NAME, RolloverAction.NAME).build()}, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants the minimum privileges required to write data into the monitoring indices (.monitoring-*). " + "This role also has the privileges necessary to create Metricbeat indices (metricbeat-*) " + "and write data into them.")), entry("remote_monitoring_collector", new RoleDescriptor("remote_monitoring_collector", new String[]{"monitor"}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices("*").privileges("monitor").allowRestrictedIndices(true).build(), RoleDescriptor.IndicesPrivileges.builder().indices(".kibana*").privileges("read").allowRestrictedIndices(true).build()}, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants the minimum privileges required to collect monitoring data for the Elastic Stack.")), entry("ingest_admin", new RoleDescriptor("ingest_admin", new String[]{"manage_index_templates", "manage_pipeline"}, null, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants access to manage all index templates and all ingest pipeline configurations.")), entry("reporting_user", new RoleDescriptor("reporting_user", null, null, null, null, null, MetadataUtils.getDeprecatedReservedMetadata("Please use Kibana feature privileges instead"), null, null, null, null, "Grants the specific privileges required for users of X-Pack reporting other than those required to use Kibana. " + "This role grants access to the reporting indices; each user has access to only their own reports. " + "Reporting users should also be assigned additional roles that grant access to Kibana as well as read access " + "to the indices that will be used to generate reports.")), entry(KibanaSystemUser.ROLE_NAME, kibanaSystemRoleDescriptor(KibanaSystemUser.ROLE_NAME)), entry("logstash_system", new RoleDescriptor("logstash_system", new String[]{"monitor", MonitoringBulkAction.NAME}, null, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants access necessary for the Logstash system user to send system-level data (such as monitoring) to Elasticsearch. " + "This role should not be assigned to users as the granted permissions may change between releases.")), entry("beats_admin", new RoleDescriptor("beats_admin", null, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices(".management-beats").privileges("all").build()}, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants access to the .management-beats index, which contains configuration information for the Beats.")), entry(UsernamesField.BEATS_ROLE, new RoleDescriptor(UsernamesField.BEATS_ROLE, new String[]{"monitor", MonitoringBulkAction.NAME}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices(".monitoring-beats-*").privileges("create_index", "create").build()}, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants access necessary for the Beats system user to send system-level data (such as monitoring) to Elasticsearch. " + "This role should not be assigned to users as the granted permissions may change between releases.")), entry(UsernamesField.APM_ROLE, new RoleDescriptor(UsernamesField.APM_ROLE, new String[]{"monitor", MonitoringBulkAction.NAME}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices(".monitoring-beats-*").privileges("create_index", "create_doc").build()}, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants access necessary for the APM system user to send system-level data (such as monitoring) to Elasticsearch.")), entry("apm_user", new RoleDescriptor("apm_user", null, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices("apm-*").privileges("read", "view_index_metadata").build(), RoleDescriptor.IndicesPrivileges.builder().indices("logs-apm.*").privileges("read", "view_index_metadata").build(), RoleDescriptor.IndicesPrivileges.builder().indices("logs-apm-*").privileges("read", "view_index_metadata").build(), RoleDescriptor.IndicesPrivileges.builder().indices("metrics-apm.*").privileges("read", "view_index_metadata").build(), RoleDescriptor.IndicesPrivileges.builder().indices("metrics-apm-*").privileges("read", "view_index_metadata").build(), RoleDescriptor.IndicesPrivileges.builder().indices("traces-apm.*").privileges("read", "view_index_metadata").build(), RoleDescriptor.IndicesPrivileges.builder().indices("traces-apm-*").privileges("read", "view_index_metadata").build(), RoleDescriptor.IndicesPrivileges.builder().indices(".ml-anomalies*").privileges("read", "view_index_metadata").build(), RoleDescriptor.IndicesPrivileges.builder().indices("observability-annotations").privileges("read", "view_index_metadata").build()}, new RoleDescriptor.ApplicationResourcePrivileges[]{RoleDescriptor.ApplicationResourcePrivileges.builder().application("kibana-*").resources("*").privileges("reserved_ml_apm_user").build()}, null, null, MetadataUtils.getDeprecatedReservedMetadata("This role will be removed in a future major release. Please use editor and viewer roles instead"), null, null, null, null, "Grants the privileges required for APM users (such as read and view_index_metadata privileges " + "on the apm-* and .ml-anomalies* indices).")), entry("inference_admin", new RoleDescriptor("inference_admin", new String[]{"manage_inference"}, null, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants access necessary to manage inference models and performing inference.")), entry("inference_user", new RoleDescriptor("inference_user", new String[]{"monitor_inference"}, null, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants access necessary to perform inference.")), entry("machine_learning_user", new RoleDescriptor("machine_learning_user", new String[]{"monitor_ml"}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices(".ml-anomalies*", ".ml-notifications*").privileges("view_index_metadata", "read").build(), RoleDescriptor.IndicesPrivileges.builder().indices(".ml-annotations*").privileges("view_index_metadata", "read", "write").build()}, new RoleDescriptor.ApplicationResourcePrivileges[]{RoleDescriptor.ApplicationResourcePrivileges.builder().application("kibana-*").resources("*").privileges("reserved_ml_user").build()}, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants the minimum privileges required to view machine learning configuration, status, and work with results. " + "This role grants monitor_ml cluster privileges, read access to the .ml-notifications and .ml-anomalies* indices " + "(which store machine learning results), and write access to .ml-annotations* indices. " + "Machine learning users also need index privileges for source and destination indices " + "and roles that grant access to Kibana. ")), entry("machine_learning_admin", new RoleDescriptor("machine_learning_admin", new String[]{"manage_ml"}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices(".ml-anomalies*", ".ml-notifications*", ".ml-state*", ".ml-meta*", ".ml-stats-*").allowRestrictedIndices(true).privileges("view_index_metadata", "read").build(), RoleDescriptor.IndicesPrivileges.builder().indices(".ml-annotations*").privileges("view_index_metadata", "read", "write").build()}, new RoleDescriptor.ApplicationResourcePrivileges[]{RoleDescriptor.ApplicationResourcePrivileges.builder().application("kibana-*").resources("*").privileges("reserved_ml_admin").build()}, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Provides all of the privileges of the machine_learning_user role plus the full use of the machine learning APIs. " + "Grants manage_ml cluster privileges, read access to .ml-anomalies*, .ml-notifications*, .ml-state*, " + ".ml-meta* indices and write access to .ml-annotations* indices. " + "Machine learning administrators also need index privileges for source and destination indices " + "and roles that grant access to Kibana.")), entry("data_frame_transforms_admin", new RoleDescriptor("data_frame_transforms_admin", new String[]{"manage_data_frame_transforms"}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices(TransformInternalIndexConstants.AUDIT_INDEX_PATTERN, TransformInternalIndexConstants.AUDIT_INDEX_PATTERN_DEPRECATED, TransformInternalIndexConstants.AUDIT_INDEX_READ_ALIAS).privileges("view_index_metadata", "read").build()}, new RoleDescriptor.ApplicationResourcePrivileges[]{RoleDescriptor.ApplicationResourcePrivileges.builder().application("kibana-*").resources("*").privileges("reserved_ml_user").build()}, null, null, MetadataUtils.getDeprecatedReservedMetadata("Please use the [transform_admin] role instead"), null, null, null, null, "Grants manage_data_frame_transforms cluster privileges, which enable you to manage transforms. " + "This role also includes all Kibana privileges for the machine learning features.")), entry("data_frame_transforms_user", new RoleDescriptor("data_frame_transforms_user", new String[]{"monitor_data_frame_transforms"}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices(TransformInternalIndexConstants.AUDIT_INDEX_PATTERN, TransformInternalIndexConstants.AUDIT_INDEX_PATTERN_DEPRECATED, TransformInternalIndexConstants.AUDIT_INDEX_READ_ALIAS).privileges("view_index_metadata", "read").build()}, new RoleDescriptor.ApplicationResourcePrivileges[]{RoleDescriptor.ApplicationResourcePrivileges.builder().application("kibana-*").resources("*").privileges("reserved_ml_user").build()}, null, null, MetadataUtils.getDeprecatedReservedMetadata("Please use the [transform_user] role instead"), null, null, null, null, "Grants monitor_data_frame_transforms cluster privileges, which enable you to use transforms. " + "This role also includes all Kibana privileges for the machine learning features. ")), entry("transform_admin", new RoleDescriptor("transform_admin", new String[]{"manage_transform"}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices(TransformInternalIndexConstants.AUDIT_INDEX_PATTERN, TransformInternalIndexConstants.AUDIT_INDEX_PATTERN_DEPRECATED, TransformInternalIndexConstants.AUDIT_INDEX_READ_ALIAS).privileges("view_index_metadata", "read").build()}, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants manage_transform cluster privileges, which enable you to manage transforms. " + "This role also includes all Kibana privileges for the machine learning features.")), entry("transform_user", new RoleDescriptor("transform_user", new String[]{"monitor_transform"}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices(TransformInternalIndexConstants.AUDIT_INDEX_PATTERN, TransformInternalIndexConstants.AUDIT_INDEX_PATTERN_DEPRECATED, TransformInternalIndexConstants.AUDIT_INDEX_READ_ALIAS).privileges("view_index_metadata", "read").build()}, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants monitor_transform cluster privileges, which enable you to perform read-only operations related to " + "transforms. This role also includes all Kibana privileges for the machine learning features.")), entry("watcher_admin", new RoleDescriptor("watcher_admin", new String[]{"manage_watcher"}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices(Watch.INDEX, TriggeredWatchStoreField.INDEX_NAME, HistoryStoreField.INDEX_PREFIX + "*").privileges("read").allowRestrictedIndices(true).build()}, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Allows users to create and execute all Watcher actions. " + "Grants read access to the .watches index. Also grants read access " + "to the watch history and the triggered watches index.")), entry("watcher_user", new RoleDescriptor("watcher_user", new String[]{"monitor_watcher"}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices(Watch.INDEX).privileges("read").allowRestrictedIndices(true).build(), RoleDescriptor.IndicesPrivileges.builder().indices(HistoryStoreField.INDEX_PREFIX + "*").privileges("read").build()}, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants read access to the .watches index, the get watch action and the watcher stats.")), entry("logstash_admin", new RoleDescriptor("logstash_admin", new String[]{"manage_logstash_pipelines"}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices(".logstash*").privileges("create", "delete", "index", "manage", "read").allowRestrictedIndices(true).build()}, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants access to the .logstash* indices for managing configurations, " + "and grants necessary access for logstash-specific APIs exposed by the logstash x-pack plugin.")), entry("rollup_user", new RoleDescriptor("rollup_user", new String[]{"monitor_rollup"}, null, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants monitor_rollup cluster privileges, which enable you to perform read-only operations related to rollups.")), entry("rollup_admin", new RoleDescriptor("rollup_admin", new String[]{"manage_rollup"}, null, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants manage_rollup cluster privileges, which enable you to manage and execute all rollup actions.")), entry("snapshot_user", new RoleDescriptor("snapshot_user", new String[]{"create_snapshot", GetRepositoriesAction.NAME}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices("*").privileges("view_index_metadata").allowRestrictedIndices(true).build()}, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants the necessary privileges to create snapshots of all the indices and to view their metadata. " + "This role enables users to view the configuration of existing snapshot repositories and snapshot details. " + "It does not grant authority to remove or add repositories or to restore snapshots. " + "It also does not enable to change index settings or to read or update data stream or index data.")), entry("enrich_user", new RoleDescriptor("enrich_user", new String[]{"manage_enrich", "manage_ingest_pipelines", "monitor"}, new RoleDescriptor.IndicesPrivileges[]{RoleDescriptor.IndicesPrivileges.builder().indices(".enrich-*").privileges("read", "view_index_metadata").allowRestrictedIndices(true).build(), RoleDescriptor.IndicesPrivileges.builder().indices(".enrich-*").privileges("manage", "write").build()}, null, null, null, MetadataUtils.DEFAULT_RESERVED_METADATA, null, null, null, null, "Grants access to manage all enrich indices (.enrich-*) and all operations on ingest pipelines.")), entry("viewer", buildViewerRoleDescriptor()), entry("editor", buildEditorRoleDescriptor()));
    }
}
