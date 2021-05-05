
import java.io.*;
import java.util.*;

/*
dedup() compresses a files and stores it in this format:
[unique 1kb chunk][unique 1kb chunk]  ....[unique 1kb chunk]
[new-line character]
0 3 7, 1 2 5,  ....4

All unique 1kb chunks are stored in an array (see static variable dataChunks)
Data after the new-line character is meta data (see static variable metaData)

redup() extracts files stored in the above-mentioned format.
 */
class Challenge {
    /*
     * ByteChunk encapsulates a byte array.
     * The primary purpose of ByteChunk is to determine unique byte arrays.
     * It provides a interface to "compare" one byte array with another.
     * "compare" determines where any two byte arrays are equal (i.e. identical)
     * "compare" also orders byte array.  Ordering is required in Collections.
     */
    static class ByteChunk implements Comparator<ByteChunk> {
      byte[] byteChunk;
      
      ByteChunk(byte[] byteChunk) {
        super();
        this.byteChunk = byteChunk;
      }
     
      @Override
      public int compare(ByteChunk thisChunk, ByteChunk otherChunk) { 
        int code = 0; //this is equal to other
        byte[] thisByteChunk = thisChunk.byteChunk;
        byte[] otherByteChunk = otherChunk.byteChunk;
        for(int i = 0; i < thisByteChunk.length; i++) {
          int thisByte = (int)thisByteChunk[i];
          int otherByte = (int)otherByteChunk[i];          
          if (thisByte > otherByte) {
            code = 1; //this is bigger than other
          } else {
            if (thisByte < otherByte) {
              code = -1;  //this is smaller than other
            }
          }
          if (code != 0) { break; }
        }
        return code;
      }

      @Override
      public boolean equals(Object obj) {
        boolean isEqual = false;
        if(obj instanceof ByteChunk) {
            ByteChunk other = (ByteChunk)obj;
            int code = this.compare(this, other);
            isEqual = (code == 0) ? true : false;
        }
        return isEqual;
      }
  }

  private static void addDataChunk(byte[ ] chunk, int position){
    ByteChunk byteChunk = new ByteChunk(chunk);
    int chunkIndex = dataChunks.indexOf(byteChunk);
    boolean isDuplicate = (chunkIndex == -1) ? false : true;
    if(isDuplicate) {
      addMetaData(chunkIndex, position);
    } else {
      dataChunks.add(byteChunk);
      chunkIndex = dataChunks.size() - 1;
      addMetaData(chunkIndex, position);
    }
    return;
  }

  private static void addMetaData(int chunkIndex, int position) {
    boolean isDuplicate = metaData.containsKey(Integer.valueOf(chunkIndex));
    if(isDuplicate) {
      List<Integer> positions = metaData.get(Integer.valueOf(chunkIndex));
      positions.add(Integer.valueOf(position));
    } else {
      List<Integer> positions = new ArrayList<Integer>();
      positions.add(Integer.valueOf(position));
      metaData.put(chunkIndex, positions);
    }
    return;
  }

  private static byte[ ] compress() throws Exception{
    ByteArrayOutputStream content = new ByteArrayOutputStream();
    //1st part of compressed file      
    Iterator<ByteChunk> chunkList = dataChunks.iterator();
    while(chunkList.hasNext()) {
      ByteChunk byteChunk = chunkList.next();
      content.write(byteChunk.byteChunk);
    }
    //2nd part of compressed file - meta data
    String metaInfo = "";
    boolean isDuplicated = false; //is any chunk at all duplicated?
    Iterator<Integer> chunkIndices = metaData.keySet().iterator();
    while(chunkIndices.hasNext()) {
      Integer chunkIndex = chunkIndices.next();
      List<Integer> positions = metaData.get(chunkIndex);
      if(!isDuplicated) {//none of the preceeding chunks are duplicated
        isDuplicated = (positions.size() > 1)? true : false;  //this chunk is duplicated
      }
      Iterator<Integer> positionItems = positions.iterator();
      while(positionItems.hasNext()) {
        Integer position = positionItems.next();
          metaInfo = metaInfo + " " + position.intValue();
      }
      metaInfo = metaInfo + ",";
    }
    if(isDuplicated) {
      metaInfo = metaInfo.substring(0, metaInfo.length() - 1); //drop the trailing ','
      metaInfo = metaInfo.substring(1); //drop the leading space
      byte[ ] newLine = {(byte)10};  //decimal 10 is ASCII new-line character
      content.write(newLine);      
      content.write(metaInfo.getBytes());
    }
    content.close();
    return content.toByteArray();
  }

  private static byte[ ] extract() throws Exception{
    TreeMap<Integer, Integer> sequence = new TreeMap<Integer, Integer>();
    Iterator<Integer> chunks = metaData.keySet().iterator();
    while(chunks.hasNext()) {
      Integer chunk = chunks.next();
      List<Integer> allPositions = metaData.get(chunk);
      Iterator<Integer> positions = allPositions.iterator();
      while(positions.hasNext()) {
        Integer position = positions.next();
        sequence.put(position, chunk);
      }
    }

    ByteArrayOutputStream content = new ByteArrayOutputStream();
    Iterator<Integer> fileChunks = sequence.keySet().iterator();
    while(fileChunks.hasNext()){
      Integer index = fileChunks.next();
      ByteChunk byteChunk = dataChunks.get(sequence.get(index));
      content.write(byteChunk.byteChunk);
    }
    content.close();
    return content.toByteArray();
  }  

	public static void dedup(String inpath, String outpath) {
    dataChunks = new ArrayList<ByteChunk>();
    metaData = new HashMap<Integer, List<Integer>>();
    try {
      FileInputStream instream = new FileInputStream(inpath);      
      byte[] chunk = new byte[CHUNK_SIZE];
      int keepReading = instream.read(chunk);
      int position = 0;
      while (keepReading != -1) {
        addDataChunk(chunk, position);
        position = position + 1;
        chunk = new byte[CHUNK_SIZE];
        keepReading = instream.read(chunk);
      }
      instream.close();

      FileOutputStream outstream = new FileOutputStream(outpath);
      outstream.write(compress());       
      outstream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return;
	}

  private static HashMap<Integer, List<Integer>> getMetaData(String inpath, long skip) throws Exception{
    FileInputStream instream = new FileInputStream(inpath);
    ByteArrayOutputStream content = new ByteArrayOutputStream();
    byte[] metaByte = new byte[1];
    instream.skip(skip);
    int keepReading = instream.read(metaByte);
    while (keepReading != -1) {
      content.write(metaByte);
      metaByte = new byte[1];
      keepReading = instream.read(metaByte);
    }    
    content.close();
    instream.close();
    String metaInfo = new String(content.toByteArray());
    metaInfo = metaInfo.substring(1); //drop the leading new-line character

    HashMap<Integer, List<Integer>> metaData = new HashMap<Integer, List<Integer>>();
    String[] chunkIndices = metaInfo.split(",");
    for(int i = 0; i < chunkIndices.length; i++){
      String chunk = chunkIndices[i];
      String[] positions = chunk.trim().split(" ");
      for(int j = 0; j < positions.length; j++) {
        String position = positions[j];
        if(metaData.containsKey(Integer.valueOf(i))) {
          List<Integer> allPositions = (List<Integer>)metaData.get(Integer.valueOf(i));
          allPositions.add(Integer.valueOf(position));
        } else {
          Integer chunkIndex = Integer.valueOf(i);
          ArrayList<Integer> allPositions = new ArrayList<Integer>();
          allPositions.add(Integer.valueOf(position));
          metaData.put(chunkIndex, allPositions);
        }
      }
    }
    return metaData;
  }

  private static boolean isMetaData(byte[] chunk) {
    boolean isMetaData = false;
    int firstByte = (int)chunk[0];
    int secondByte = (int)chunk[1];
    if (firstByte == 10 && secondByte == 48) {
      //ASCII new-line followed by decimal 0
      isMetaData = true;
    }
    return isMetaData;
  }

	public static void redup(String inpath, String outpath) {
    dataChunks = new ArrayList<ByteChunk>();
    metaData = new HashMap<Integer, List<Integer>>();
    try {
      FileInputStream instream = new FileInputStream(inpath);      
      byte[] chunk = new byte[CHUNK_SIZE];
      int keepReading = instream.read(chunk);
      int position = 0;
      while (keepReading != -1) {
        if(isMetaData(chunk)) {
          instream.close();
          long skip = position * CHUNK_SIZE;
          metaData = getMetaData(inpath, skip);
          keepReading = -1;
        } else {
          addDataChunk(chunk, position);
          position = position + 1;
          chunk = new byte[CHUNK_SIZE];
          keepReading = instream.read(chunk);   
        }
      }
      instream.close();

      FileOutputStream outstream = new FileOutputStream(outpath);
      outstream.write(extract());       
      outstream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return;
  }  

  private static ArrayList<ByteChunk> dataChunks;

  private static HashMap<Integer, List<Integer>> metaData;  
    
  private static final int CHUNK_SIZE = 1024;    
}

