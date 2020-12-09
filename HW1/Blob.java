package HW1;

public class Blob extends Key_Value_Storage{
	String key;
	private String filename;

	public Blob(String filename) {
		this.filename = filename;
	}
	
	//blob对象的add方法，调用父类addFile方法
	public void add() {
		this.key = super.addFile(this.filename);
		if (this.key != null) {
			System.out.println(this.key + " " + this.filename);
		} else {
			System.out.println("Failed");
		}
	}
	
	
	@Override
	public String toString() {
		return "100644 blob " + this.key;
	}
	

}
