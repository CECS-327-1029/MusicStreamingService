package streamingservice.DFS;

import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.*;
import java.nio.file.*;
import java.math.BigInteger;
import java.security.*;
// import a json package


/* JSON Format

 {
    "metadata" :
    {
        file :
        {
            name  : "File1"
            numberOfPages : "3"
            pageSize : "1024"
            size : "2291"
            page :
            {
                number : "1"
                guid   : "22412"
                size   : "1024"
            }
            page :
            {
                number : "2"
                guid   : "46312"
                size   : "1024"
            }
            page :
            {
                number : "3"
                guid   : "93719"
                size   : "243"
            }
        }
    }
}
 
 
 */


public class DFS
{
    private int port;
    private Chord chord;
    
    private long md5(String objectName)
    {
        try
        {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(objectName.getBytes());
            BigInteger bigInt = new BigInteger(1,m.digest());
            return Math.abs(bigInt.longValue());
        }
        catch(NoSuchAlgorithmException e)
        {
                e.printStackTrace();
                
        }
        return 0;
    }

    public DFS(int port) throws Exception {
        
        this.port = port;
        long guid = md5("" + port);
        chord = new Chord(port, guid);
        Files.createDirectories(Paths.get(guid + "/repository"));
        Files.createDirectories(Paths.get(guid + "/tmp"));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            chord.leave();
        }));
    }
    
    public void join(String Ip, int port) throws Exception {
        chord.joinRing(Ip, port);
        chord.print();
    }

    public FilesJson readMetadata() throws Exception {
        FilesJson filesJson;
        try {
            long guid = md5("Metadata");
            ChordMessageInterface peer = chord.locateSuccessor(guid);
            RemoteInputFileStream metadataraw = peer.get(guid);
            metadataraw.connect();
            Scanner scanner = new Scanner(metadataraw);
            scanner.useDelimiter("\\A");
            String strMetadata = scanner.next();
            System.out.println(strMetadata);
            filesJson = new Gson().fromJson(strMetadata, FilesJson.class);
        } catch (Exception e) {
            filesJson = new FilesJson();
        }
        return filesJson;
    }
    
    public void writeMetaData(FilesJson filesJson) throws Exception
    {
        long guid = md5("Metadata");
        ChordMessageInterface peer = chord.locateSuccessor(guid);
        peer.put(guid, new Gson().toJson(filesJson));
    }

    public void mv(String oldName, String newName) throws Exception {
        boolean doesFileExist = false;
        FilesJson metadata = this.readMetadata();

        for (int i = 0; i < metadata.getSize() && !doesFileExist; i++) {
            FileJson file = metadata.getFile(i);
            if (file.getFileName().equalsIgnoreCase(oldName)) {
                file.setFileName(newName);
                file.setWriteTimeStamp(LocalDateTime.now().toString());
                doesFileExist = true;
            }
        }

        if (doesFileExist) {
            System.out.println(oldName + " was renamed to " + newName + " successfully.");
        } else {
            System.out.println("Couldn't rename " + oldName + " to " + newName + ".");
        }
    }

    public String ls() throws Exception {
        FilesJson metadata = readMetadata();
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < metadata.getSize(); i++) {
            list.append(metadata.getFile(i).getFileName()).append("\n");
        }
        return list.toString();
    }

    public void touch(String fileName) throws Exception {
        FilesJson metadata = readMetadata();
        FileJson newFile = new FileJson();
        newFile.setFileName(fileName);
        metadata.addFile(newFile);
        writeMetaData(metadata);
    }

    public void delete(String fileName) throws Exception {
        FilesJson metadata = readMetadata();
        FileJson file = metadata.getFile(fileName);
        if (file != null) {
            if (file.getNumberOfPages() > 0) {
                for (int i = 0; i < file.getNumberOfPages(); i++) {
                    PagesJson page = file.getPages().get(i);
                    long guid = page.getGuid();
                    chord.locateSuccessor(guid).delete(guid);
                }
                writeMetaData(metadata);
            }
        }
    }
    
    public Byte[] read(String fileName, int pageNumber) throws Exception {
        FilesJson metadata = readMetadata();

        FileJson file = metadata.getFile(fileName);
        if (file != null) {
            PagesJson page = file.getPages().get(--pageNumber);
            String readTime = LocalDateTime.now().toString();
            page.setReadTimeStamp(readTime);
            file.setReadTimeStamp(readTime);
            Long pageGuid = page.getGuid();
            byte[] byteData = chord.locateSuccessor(pageGuid).get(pageGuid).buf;
            Byte[] readData = new Byte[byteData.length];
            int i = 0;
            for (byte b :  byteData) { readData[i++] = b; }
            return readData;
        }
        return null;
    }

    public Byte[] tail(String fileName) throws Exception {
        FileJson file = readMetadata().getFile(fileName);
        return file != null ? read(fileName, file.getNumberOfPages() - 1) : null;
    }

    public Byte[] head(String fileName) throws Exception {
        FileJson file = readMetadata().getFile(fileName);
        return file != null ? read(fileName, 0) : null;
    }

    public void append(String filename, Byte[] data) throws Exception {
        FilesJson metadata = readMetadata();
        FileJson file = metadata.getFile(filename);
        if (file != null) {
            long sizeOfData = data.length;
            String appendTime = LocalDateTime.now().toString();
            file.setWriteTimeStamp(appendTime);
            file.setNumberOfPages(file.getNumberOfPages() + 1);
            file.setSize(file.getSize() + sizeOfData);
            Long guid = md5(filename + LocalDateTime.now().toString());
            byte[] bdata = new byte[data.length];
            int i = 0;
            for (Byte b : data) bdata[i++] = b.byteValue();
            chord.locateSuccessor(guid).put(guid, Base64.getEncoder().encodeToString(bdata));
            file.addPage(guid, sizeOfData, appendTime, "0", 0);
            writeMetaData(metadata);
        }
    }

    public void print() {
        chord.print();
    }

    public void leave() { chord.leave(); }

    public static class PagesJson {

        private Long guid;
        private Long size;
        private String writeTimeStamp;
        private String readTimeStamp;
        private int referenceCount;

        public PagesJson(Long guid, Long size, String writeTimeStamp, String readTimeStamp, int referenceCount) {
            this.guid = guid;
            this.size = size;
            this.writeTimeStamp = writeTimeStamp;
            this.readTimeStamp = readTimeStamp;
            this.referenceCount = referenceCount;
        }

        public Long getGuid() {
            return guid;
        }

        public void setGuid(Long guid) {
            this.guid = guid;
        }

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public String getWriteTimeStamp() {
            return writeTimeStamp;
        }

        public void setWriteTimeStamp(String writeTimeStamp) {
            this.writeTimeStamp = writeTimeStamp;
        }

        public String getReadTimeStamp() {
            return readTimeStamp;
        }

        public void setReadTimeStamp(String readTimeStamp) {
            this.readTimeStamp = readTimeStamp;
        }

        public int getReferenceCount() {
            return referenceCount;
        }

        public void setReferenceCount(int referenceCount) {
            this.referenceCount = referenceCount;
        }
    }

    public static class FileJson {

        private String fileName;
        private Long size;
        private String writeTimeStamp;
        private String readTimeStamp;
        private int referenceCount;
        private int numberOfPages;
        private int maxPageSize;
        private ArrayList<PagesJson> pages;

        public FileJson() {
            this.size = 0L;
            this.writeTimeStamp = "0";
            this.readTimeStamp = "0";
            this.referenceCount = 0;
            this.numberOfPages = 0;
            this.maxPageSize = 0;
            this.pages = new ArrayList<>();
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public String getWriteTimeStamp() {
            return writeTimeStamp;
        }

        public void setWriteTimeStamp(String writeTimeStamp) {
            this.writeTimeStamp = writeTimeStamp;
        }

        public String getReadTimeStamp() {
            return readTimeStamp;
        }

        public void setReadTimeStamp(String readTimeStamp) {
            this.readTimeStamp = readTimeStamp;
        }

        public int getReferenceCount() {
            return referenceCount;
        }

        public void setReferenceCount(int referenceCount) {
            this.referenceCount = referenceCount;
        }

        public int getNumberOfPages() {
            return numberOfPages;
        }

        public void setNumberOfPages(int numberOfPages) {
            this.numberOfPages = numberOfPages;
        }

        public int getMaxPageSize() {
            return maxPageSize;
        }

        public void setMaxPageSize(int maxPageSize) {
            this.maxPageSize = maxPageSize;
        }

        public ArrayList<PagesJson> getPages() {
            return pages;
        }

        public void setPages(ArrayList<PagesJson> pages) {
            this.pages = pages;
        }

        public void addPage(Long guid, Long size, String writeTimeStamp, String readTimeStamp, int referenceCount) {
            PagesJson newPage = new PagesJson(guid, size, writeTimeStamp, readTimeStamp, referenceCount);
            pages.add(newPage);
        }
    }

    public static class FilesJson {

        private List<FileJson> metadata;

        public FilesJson() {
            this.metadata = new ArrayList<>();
        }

        public int getSize() { return metadata.size(); }

        public FileJson getFile(int index) {
            if (index < 0 || index >= metadata.size()) return null;
            return metadata.get(index);
        }

        public FileJson getFile(String filename) {
            return metadata.stream()
                    .filter(file -> file.getFileName().equalsIgnoreCase(filename))
                    .findFirst().orElse(null);
        }

        public void addFile(FileJson file) { metadata.add(file); }

        public boolean containsFile(String fileName) {
            return metadata.stream().anyMatch(file -> file.getFileName().equals(fileName));
        }

        public void deleteFile(String filename) {
            metadata.removeIf(fileJson -> fileJson.getFileName().equals(filename));
        }

        public void clearAll() { metadata.clear(); }
    }

}
