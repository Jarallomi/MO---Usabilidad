package mo.analysis;

public class Note implements Comparable<Note>{
	private String comment;
	private long beginTime;
	private long endTime;

	public Note(long beginTime, long endTime, String comment) {
		this.comment = comment;
		this.beginTime = beginTime;
		this.endTime = endTime;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public void setStartTime(long beginTime) {
		this.beginTime = beginTime;
	}

	public long getStartTime() {
		return beginTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getEndTime() {
		return endTime;
	}

	@Override
	public int compareTo(Note other) {
		if(this.beginTime < other.beginTime) {
			return -1;
		}

		if (this.beginTime > other.beginTime) {
			return 1;
		}

		return 0;
	}

	@Override
	public String toString() {
		return beginTime + "," + endTime + "," + comment;
	}
}