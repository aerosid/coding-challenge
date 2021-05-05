import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.Arrays;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.security.*;

public class Tests {
    private String original;
    private String deduped;
    private String reduped;
    
    private final int CHUNK_SIZE = 1024;
    private byte[] chunk = new byte[CHUNK_SIZE];
    private Random random = new Random();
    
    @Before
    public void init() {
        try{
            original = Files.createTempFile(null, null).toString();
            deduped = Files.createTempFile(null, null).toString();
            reduped = Files.createTempFile(null, null).toString();
        }
        catch (IOException e) {}
    }       

    public String writeFile(String path, List<byte[]> chunks) {
        try {
            MessageDigest hasher = MessageDigest.getInstance("MD5");
            
            FileOutputStream stream = new FileOutputStream(path);
            for (byte[] chunk : chunks) {
                stream.write(chunk);
                hasher.update(chunk);
            }
            
            return new String(hasher.digest());
        }
        catch (IOException e) {}
        catch (NoSuchAlgorithmException e) {}
        
        return null;
    }
    
    public String readFile(String path) {
        try {
            MessageDigest hasher = MessageDigest.getInstance("MD5");            
            byte[] data = Files.readAllBytes(Paths.get(path));
            return new String(hasher.digest(data));
        }
        catch (IOException e) {}
        catch (NoSuchAlgorithmException e) {}
        
        return null;
    }
    
    public void process(List<byte[]> chunks) {
        String before = writeFile(original, chunks);        
        Challenge.dedup(original, deduped);
        
        long[] size = {new File(original).length(), new File(deduped).length()};
        assertTrue("Assertion failure -- Deduplicated file is larger than the original.", size[0] >= size[1]);
        System.out.printf("Original Size %d bytes -> Deduped Size %d bytes\n", size[0], size[1]);
        
        Challenge.redup(deduped, reduped);        
        String after = readFile(reduped);
        assertEquals("Assertion failure -- Reduplicated file is not the same as the original.", before, after);                
    }
    
	@Test
	public void basicTest() {        
        random.setSeed(0);
        byte[] chunk = new byte[CHUNK_SIZE];
        random.nextBytes(chunk);
        List<byte[]> chunks = Arrays.asList(chunk, chunk, chunk);
        process(chunks);
	}
    
	@Test
	public void smallFile() {        
        random.setSeed(0);        
        List<byte[]> pool = new ArrayList<byte[]>();
        for (int i = 0; i < 2; i++) {
            byte[] chunk = new byte[CHUNK_SIZE];
            random.nextBytes(chunk);
            pool.add(chunk);
        }

        List<byte[]> chunks = Arrays.asList(pool.get(0), pool.get(1), pool.get(0), pool.get(0), pool.get(1));
        process(chunks);
	}
  
  
    public void crunch(List<byte[]> chunks) {
        String before = writeFile(original, chunks);        
        Challenge.dedup(original, deduped);
        
        long[] size = {new File(original).length(), new File(deduped).length()};
        System.out.printf("Original: %d bytes -> Deduped: %d bytes\n", size[0], size[1]);
        
        Challenge.redup(deduped, reduped);
        String after = readFile(reduped);
        assertEquals("Reduplicated not the same.", before, after);       
        return;
    }
  
	@Test
	public void codeSnippetTest() {        
        random.setSeed(0);
        byte[] chunk = new byte[CHUNK_SIZE];
        random.nextBytes(chunk);
        List<byte[]> chunks = Arrays.asList(chunk);
        crunch(chunks);
	}  
}
