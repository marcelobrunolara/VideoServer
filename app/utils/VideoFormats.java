package utils;

public class VideoFormats {
	
	public final String videoformats[] = {"mp4","dv", "flv", "mpeg", "mov", "mkv", "3gp", "3g2", "asf", "avi", "f4v",
											"ismv", "f4v", "wmv", "aac"};
	
	private static VideoFormats object;
	
	public static VideoFormats Instance(){
		
		if (object==null){
			object = new VideoFormats();
		}
		return object;
	}
	
	private VideoFormats(){
		
	}

}
