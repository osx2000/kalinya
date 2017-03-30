package com.kalinya.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.kalinya.oc.util.MessageLog;

final public class Timer {
	private static final long NANO_SECONDS_TO_MILLIS = (long) 1E6;
	private boolean started = false;
	private Long startTime;
	private Long taskStartTime;
	private Map<String, Long> taskRunningTimes = new LinkedHashMap<String, Long>();
	private String taskName;
	private MessageLog messageLog;

	public Timer() {
	}

	public Timer(MessageLog messageLog) {
		this();
		this.messageLog = messageLog;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("CurrentTask", taskName)
				.append("TasksTracked", taskRunningTimes.size())
				.build();
	}
	
	public MessageLog getMessageLog() {
		return messageLog;
	}

	/**
	 * Logs the start time of the task
	 * @param taskName
	 */
	public void start(String taskName) {
		Long now = Long.valueOf(System.nanoTime());
		if(!started) {
			startTime = now;
			started = true;
		}
		if(this.taskName != null) {
			stop();
		}
		taskStartTime = now;
		this.taskName = taskName;
	}

	/**
	 * Logs the end and execution run time of the task
	 */
	public void stop() {
		if(taskName == null) {
			throw new IllegalStateException("There is no task running");
		}
		Long taskElapsedTime = (System.nanoTime()-taskStartTime)/NANO_SECONDS_TO_MILLIS;
		if(taskRunningTimes.containsKey(taskName)) {
			taskElapsedTime += taskRunningTimes.get(taskName);
		}
		taskRunningTimes.put(taskName, taskElapsedTime);
		taskName = null;
	}

	private void print(String message) {
		if(getMessageLog() == null) {
			System.out.println(message);
		} else {
			getMessageLog().info(message);
		}
	}
	
	/**
	 * Prints the total time and for each task, the task execution run time
	 */
	public void print(boolean sorted) {
		List<String> list = getJobTimerAsList(sorted);
		for(String message: list) {
			print(message);
		}
	}
	
	public String getJobTimerAsString(boolean sorted) {
		StringBuilder sb = new StringBuilder();
		List<String> list = getJobTimerAsList(sorted);
		String lineBreak = "";
		for(String message: list) {
			sb.append(lineBreak);
			sb.append(message);
			lineBreak = "\n";
		}
		return sb.toString();
	}
	
	public List<String> getJobTimerAsList(boolean sorted) {
		if(!started) {
			throw new IllegalStateException("There is no task running");
		}
		if(taskName != null) {
			stop();
		}
		long totalElapsedTime = getElapsedRunningTimeMillis();
		List<String> list = new ArrayList<>();
		list.add(String.format("Total elapsed time [%s]", toMinSeconds(totalElapsedTime)));
		Map<String, Long> runningTimes = null;
		if(sorted) {
			runningTimes = getSortedRunningTimes();
		} else {
			runningTimes = taskRunningTimes;
		}
		for(String taskName: runningTimes.keySet()) {
			list.add(String.format("%s   %04.1f%%   %s",
					toMinSeconds(runningTimes.get(taskName)),
					(double) 100*runningTimes.get(taskName)/totalElapsedTime,
					taskName));
		}
		return list;
	}

	/**
	 * Returns the running time from the start until now in milliseconds
	 * 
	 * @return
	 */
	public long getElapsedRunningTimeMillis() {
		return (System.nanoTime() - startTime)/NANO_SECONDS_TO_MILLIS;
	}
	
	public String getElapsedRunningTimeMinSec() {
		return toMinSeconds(getElapsedRunningTimeMillis());
	}

	public Map<String, Long> getSortedRunningTimes() {
		return CollectionUtil.sortByValue(taskRunningTimes);
	}

	/**
	 * Converts milliseconds to minutes: seconds
	 * @param millis
	 * @return
	 */
	private static String toMinSeconds(long millis) {
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		return String.format("%02dm %04.1fs", minutes, (double) millis/1000);
	}
}