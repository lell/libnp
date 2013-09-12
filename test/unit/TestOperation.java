package unit;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import static libnp.util.Operation.dump;
import static libnp.util.Operation.undump;

public class TestOperation {

	public static String getTestDir() {
		return System.getProperty("user.home") + "/test/libnp/TestOperation/";
	}
	
	@Test
	public void test_dump() {
		String test_dir = getTestDir();
		(new File(test_dir)).mkdirs();
		
		String filename = test_dir + "/dump";
		
		String content = "abcd\nefgh\nijkl";
		
		dump(filename, content);
		assertTrue(content.equals(undump(filename)));		
	}

}
