public class Example {
    public int run(String[] argv) throws Exception {
        int exitCode = -1;
        if (argv.length < 1) {
            displayUsage("");
            return exitCode;
        }
        String cmd = argv[0];
        String submitJobFile = null;
        String jobid = null;
        String taskid = null;
        String historyFileOrJobId = null;
        String historyOutFile = null;
        String historyOutFormat = HistoryViewer.HUMAN_FORMAT;
        String counterGroupName = null;
        String counterName = null;
        JobPriority jp = null;
        String taskType = null;
        String taskState = null;
        int fromEvent = 0;
        int nEvents = 0;
        int jpvalue = 0;
        String configOutFile = null;
        boolean getStatus = false;
        boolean getCounter = false;
        boolean killJob = false;
        boolean listEvents = false;
        boolean viewHistory = false;
        boolean viewAllHistory = false;
        boolean listJobs = false;
        boolean listAllJobs = false;
        boolean listActiveTrackers = false;
        boolean listBlacklistedTrackers = false;
        boolean displayTasks = false;
        boolean killTask = false;
        boolean failTask = false;
        boolean setJobPriority = false;
        boolean logs = false;
        boolean downloadConfig = false;
        if ("-submit".equals(cmd)) {
            if (argv.length != 2) {
                displayUsage(cmd);
                return exitCode;
            }
            submitJobFile = argv[1];
        } else if ("-status".equals(cmd)) {
            if (argv.length != 2) {
                displayUsage(cmd);
                return exitCode;
            }
            jobid = argv[1];
            getStatus = true;
        } else if ("-counter".equals(cmd)) {
            if (argv.length != 4) {
                displayUsage(cmd);
                return exitCode;
            }
            getCounter = true;
            jobid = argv[1];
            counterGroupName = argv[2];
            counterName = argv[3];
        } else if ("-kill".equals(cmd)) {
            if (argv.length != 2) {
                displayUsage(cmd);
                return exitCode;
            }
            jobid = argv[1];
            killJob = true;
        } else if ("-set-priority".equals(cmd)) {
            if (argv.length != 3) {
                displayUsage(cmd);
                return exitCode;
            }
            jobid = argv[1];
            try {
                jp = JobPriority.valueOf(argv[2]);
            } catch (IllegalArgumentException iae) {
                try {
                    jpvalue = Integer.parseInt(argv[2]);
                } catch (NumberFormatException ne) {
                    LOG.info("Error number format: ", ne);
                    displayUsage(cmd);
                    return exitCode;
                }
            }
            setJobPriority = true;
        } else if ("-events".equals(cmd)) {
            if (argv.length != 4) {
                displayUsage(cmd);
                return exitCode;
            }
            jobid = argv[1];
            fromEvent = Integer.parseInt(argv[2]);
            nEvents = Integer.parseInt(argv[3]);
            listEvents = true;
        } else if ("-history".equals(cmd)) {
            viewHistory = true;
            if (argv.length < 2 || argv.length > 7) {
                displayUsage(cmd);
                return exitCode;
            }
            int index = 1;
            if ("all".equals(argv[index])) {
                index++;
                viewAllHistory = true;
                if (argv.length == 2) {
                    displayUsage(cmd);
                    return exitCode;
                }
            }
            historyFileOrJobId = argv[index++];
            if (argv.length > index + 1 && "-outfile".equals(argv[index])) {
                index++;
                historyOutFile = argv[index++];
            }
            if (argv.length > index + 1 && "-format".equals(argv[index])) {
                index++;
                historyOutFormat = argv[index++];
            }
            if (argv.length > index) {
                displayUsage(cmd);
                return exitCode;
            }
        } else if ("-list".equals(cmd)) {
            if (argv.length != 1 && !(argv.length == 2 && "all".equals(argv[1]))) {
                displayUsage(cmd);
                return exitCode;
            }
            if (argv.length == 2 && "all".equals(argv[1])) {
                listAllJobs = true;
            } else {
                listJobs = true;
            }
        } else if ("-kill-task".equals(cmd)) {
            if (argv.length != 2) {
                displayUsage(cmd);
                return exitCode;
            }
            killTask = true;
            taskid = argv[1];
        } else if ("-fail-task".equals(cmd)) {
            if (argv.length != 2) {
                displayUsage(cmd);
                return exitCode;
            }
            failTask = true;
            taskid = argv[1];
        } else if ("-list-active-trackers".equals(cmd)) {
            if (argv.length != 1) {
                displayUsage(cmd);
                return exitCode;
            }
            listActiveTrackers = true;
        } else if ("-list-blacklisted-trackers".equals(cmd)) {
            if (argv.length != 1) {
                displayUsage(cmd);
                return exitCode;
            }
            listBlacklistedTrackers = true;
        } else if ("-list-attempt-ids".equals(cmd)) {
            if (argv.length != 4) {
                displayUsage(cmd);
                return exitCode;
            }
            jobid = argv[1];
            taskType = argv[2];
            taskState = argv[3];
            displayTasks = true;
            if (!taskTypes.contains(org.apache.hadoop.util.StringUtils.toUpperCase(taskType))) {
                System.out.println("Error: Invalid task-type: " + taskType);
                displayUsage(cmd);
                return exitCode;
            }
            if (!taskStates.contains(org.apache.hadoop.util.StringUtils.toLowerCase(taskState))) {
                System.out.println("Error: Invalid task-state: " + taskState);
                displayUsage(cmd);
                return exitCode;
            }
        } else if ("-logs".equals(cmd)) {
            if (argv.length == 2 || argv.length == 3) {
                logs = true;
                jobid = argv[1];
                if (argv.length == 3) {
                    taskid = argv[2];
                } else {
                    taskid = null;
                }
            } else {
                displayUsage(cmd);
                return exitCode;
            }
        } else if ("-config".equals(cmd)) {
            downloadConfig = true;
            if (argv.length != 3) {
                displayUsage(cmd);
                return exitCode;
            }
            jobid = argv[1];
            configOutFile = argv[2];
        } else {
            displayUsage(cmd);
            return exitCode;
        }
        cluster = createCluster();
        try {
            if (submitJobFile != null) {
                Job job = Job.getInstance(new JobConf(submitJobFile));
                job.submit();
                System.out.println("Created job " + job.getJobID());
                exitCode = 0;
            } else if (getStatus) {
                Job job = getJob(JobID.forName(jobid));
                if (job == null) {
                    System.out.println("Could not find job " + jobid);
                } else {
                    Counters counters = job.getCounters();
                    System.out.println();
                    System.out.println(job);
                    if (counters != null) {
                        System.out.println(counters);
                    } else {
                        System.out.println("Counters not available. Job is retired.");
                    }
                    exitCode = 0;
                }
            } else if (getCounter) {
                Job job = getJob(JobID.forName(jobid));
                if (job == null) {
                    System.out.println("Could not find job " + jobid);
                } else {
                    Counters counters = job.getCounters();
                    if (counters == null) {
                        System.out.println("Counters not available for retired job " + jobid);
                        exitCode = -1;
                    } else {
                        System.out.println(getCounter(counters, counterGroupName, counterName));
                        exitCode = 0;
                    }
                }
            } else if (killJob) {
                Job job = getJob(JobID.forName(jobid));
                if (job == null) {
                    System.out.println("Could not find job " + jobid);
                } else {
                    JobStatus jobStatus = job.getStatus();
                    if (jobStatus.getState() == JobStatus.State.FAILED) {
                        System.out.println("Could not mark the job " + jobid + " as killed, as it has already failed.");
                        exitCode = -1;
                    } else if (jobStatus.getState() == JobStatus.State.KILLED) {
                        System.out.println("The job " + jobid + " has already been killed.");
                        exitCode = -1;
                    } else if (jobStatus.getState() == JobStatus.State.SUCCEEDED) {
                        System.out.println("Could not kill the job " + jobid + ", as it has already succeeded.");
                        exitCode = -1;
                    } else {
                        job.killJob();
                        System.out.println("Killed job " + jobid);
                        exitCode = 0;
                    }
                }
            } else if (setJobPriority) {
                Job job = getJob(JobID.forName(jobid));
                if (job == null) {
                    System.out.println("Could not find job " + jobid);
                } else {
                    if (jp != null) {
                        job.setPriority(jp);
                    } else {
                        job.setPriorityAsInteger(jpvalue);
                    }
                    System.out.println("Changed job priority.");
                    exitCode = 0;
                }
            } else if (viewHistory) {
                if (historyFileOrJobId.endsWith(".jhist")) {
                    viewHistory(historyFileOrJobId, viewAllHistory, historyOutFile, historyOutFormat);
                    exitCode = 0;
                } else {
                    Job job = getJob(JobID.forName(historyFileOrJobId));
                    if (job == null) {
                        System.out.println("Could not find job " + jobid);
                    } else {
                        String historyUrl = job.getHistoryUrl();
                        if (historyUrl == null || historyUrl.isEmpty()) {
                            System.out.println("History file for job " + historyFileOrJobId + " is currently unavailable.");
                        } else {
                            viewHistory(historyUrl, viewAllHistory, historyOutFile, historyOutFormat);
                            exitCode = 0;
                        }
                    }
                }
            } else if (listEvents) {
                Job job = getJob(JobID.forName(jobid));
                if (job == null) {
                    System.out.println("Could not find job " + jobid);
                } else {
                    listEvents(job, fromEvent, nEvents);
                    exitCode = 0;
                }
            } else if (listJobs) {
                listJobs(cluster);
                exitCode = 0;
            } else if (listAllJobs) {
                listAllJobs(cluster);
                exitCode = 0;
            } else if (listActiveTrackers) {
                listActiveTrackers(cluster);
                exitCode = 0;
            } else if (listBlacklistedTrackers) {
                listBlacklistedTrackers(cluster);
                exitCode = 0;
            } else if (displayTasks) {
                Job job = getJob(JobID.forName(jobid));
                if (job == null) {
                    System.out.println("Could not find job " + jobid);
                } else {
                    displayTasks(getJob(JobID.forName(jobid)), taskType, taskState);
                    exitCode = 0;
                }
            } else if (killTask) {
                TaskAttemptID taskID = TaskAttemptID.forName(taskid);
                Job job = getJob(taskID.getJobID());
                if (job == null) {
                    System.out.println("Could not find job " + jobid);
                } else if (job.killTask(taskID, false)) {
                    System.out.println("Killed task " + taskid);
                    exitCode = 0;
                } else {
                    System.out.println("Could not kill task " + taskid);
                    exitCode = -1;
                }
            } else if (failTask) {
                TaskAttemptID taskID = TaskAttemptID.forName(taskid);
                Job job = getJob(taskID.getJobID());
                if (job == null) {
                    System.out.println("Could not find job " + jobid);
                } else if (job.killTask(taskID, true)) {
                    System.out.println("Killed task " + taskID + " by failing it");
                    exitCode = 0;
                } else {
                    System.out.println("Could not fail task " + taskid);
                    exitCode = -1;
                }
            } else if (logs) {
                JobID jobID = JobID.forName(jobid);
                if (getJob(jobID) == null) {
                    System.out.println("Could not find job " + jobid);
                } else {
                    try {
                        TaskAttemptID taskAttemptID = TaskAttemptID.forName(taskid);
                        LogParams logParams = cluster.getLogParams(jobID, taskAttemptID);
                        LogCLIHelpers logDumper = new LogCLIHelpers();
                        logDumper.setConf(getConf());
                        exitCode = logDumper.dumpAContainersLogs(logParams.getApplicationId(), logParams.getContainerId(), logParams.getNodeId(), logParams.getOwner());
                    } catch (IOException e) {
                        if (e instanceof RemoteException) {
                            throw e;
                        }
                        System.out.println(e.getMessage());
                    }
                }
            } else if (downloadConfig) {
                Job job = getJob(JobID.forName(jobid));
                if (job == null) {
                    System.out.println("Could not find job " + jobid);
                } else {
                    String jobFile = job.getJobFile();
                    if (jobFile == null || jobFile.isEmpty()) {
                        System.out.println("Config file for job " + jobFile + " could not be found.");
                    } else {
                        Path configPath = new Path(jobFile);
                        FileSystem fs = FileSystem.get(getConf());
                        fs.copyToLocalFile(configPath, new Path(configOutFile));
                        exitCode = 0;
                    }
                }
            }
        } catch (RemoteException re) {
            IOException unwrappedException = re.unwrapRemoteException();
            if (unwrappedException instanceof AccessControlException) {
                System.out.println(unwrappedException.getMessage());
            } else {
                throw re;
            }
        } finally {
            cluster.close();
        }
        return exitCode;
    }
}
