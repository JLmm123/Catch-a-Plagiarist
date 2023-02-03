import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DocumentsProcessorTest {

    DocumentsProcessor dp = new DocumentsProcessor();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    
    @Test
    public void testProcessDocument() throws IOException {
        
        Map<String, List<String>> docs = dp.processDocuments("test_files", 4);
        //File1
        Map <String, List<String>> toTest = new HashMap <String, List<String>>();
        List <String> list1 = new ArrayList <String>();
        
        list1.add("thisisatest");
        list1.add("isatestdocument");
        
        toTest.put("File1.txt", list1);
        
        //File2
        List <String> list2 = new ArrayList <String>();
        
        list2.add("thisisalsoa");
        list2.add("isalsoatest");
        list2.add("alsoatestdocument");
        
        toTest.put("File2.txt", list2);
        
        assertEquals(docs, toTest); 
        
        
        docs = dp.processDocuments("test_files", 2);
        
        
        toTest = new HashMap <String, List<String>>();
        list1 = new ArrayList <String>();
        
        list1.add("thisis");
        list1.add("isa");
        list1.add("atest");
        list1.add("testdocument");
        
        toTest.put("File1.txt", list1);
        
        //File2
        list2 = new ArrayList <String>();
        
        list2.add("thisis");
        list2.add("isalso");
        list2.add("alsoa");
        list2.add("atest");
        list2.add("testdocument");
        
        toTest.put("File2.txt", list2);
        
        assertEquals(docs, toTest); 
        
        
    }
    
    @Test
    public void testStoreNGram() throws IOException {
        
        Map<String, List<String>> docs = dp.processDocuments("test_files", 4);
        List<Tuple<String, Integer>> indexList = dp.storeNGrams(docs, "test.txt");
        //dp.storeNGrams(docs, "sim.txt");
        int l1 = 0;
        int l2 = 0;
        for (int i = 0; i < indexList.size(); i++) {
            
            if (indexList.get(i).getLeft().equals("File1.txt")) {
                l1 = indexList.get(i).getRight();
            }
            if (indexList.get(i).getLeft().equals("File2.txt")) {
                l2 = indexList.get(i).getRight();
            }
            
        }
        assertEquals(l1, 28);
        assertEquals(l2, 42);
        
        
        
    //{File1.txt=[thisis, isa, atest, testfile], File2.txt=[thisis, isalso, alsoa, atest, testfile]}
    }

    @Test
    public void testCompareTo() throws IOException {
        Similarities s1 = new Similarities("File1.txt", "File3.txt");
        
        Similarities s2 = new Similarities("File3.txt", "File1.txt");
        
        assertEquals(0,s1.compareTo(s2));
        
        s1 = new Similarities("File1.txt", "File3.txt");
        
        s2 = new Similarities("File1.txt", "File3.txt");
        
        assertEquals(0,s1.compareTo(s2));
    }
    
    
    @Test
    public void testSimilarities() throws IOException {
        
        Map<String, List<String>> docs = dp.processDocuments("test_similarities", 3);
        
        List<Tuple<String, Integer>> indexList = dp.storeNGrams(docs, "similarities.txt");
        //compute similarities
        TreeSet<Similarities> simToTest =  dp.computeSimilarities("similarities.txt", indexList);
        TreeSet<Similarities> sim = new TreeSet<Similarities>();
        
        Similarities s1 = new Similarities("File1.txt", "File2.txt");
        s1.setCount(3);
        sim.add(s1);
        Similarities s2 = new Similarities("File1.txt", "File3.txt");
        s1.setCount(3);
        sim.add(s2);
        
        Similarities s3 = new Similarities("File2.txt", "File3.txt");
        s1.setCount(3);
        sim.add(s3);
        
        assertEquals(sim, simToTest);
        
        
        ///////////////////////////////////////////
        docs = dp.processDocuments("test_files", 2);
        
        indexList = dp.storeNGrams(docs, "test.txt");
        //compute similarities
        simToTest =  dp.computeSimilarities("test.txt", indexList);
        sim = new TreeSet<Similarities>();
        
        s1 = new Similarities("File1.txt", "File2.txt");
        s1.setCount(3);
        sim.add(s1);
        
        
        assertEquals(sim, simToTest);
        
        
    }

    @Test
    public void testPrintSimilarities() throws IOException {
        

        Map<String, List<String>> docs = dp.processDocuments("test_similarities", 3);
        
        List<Tuple<String, Integer>> indexList = dp.storeNGrams(docs, "similarities.txt");
        //compute similarities
        TreeSet<Similarities> simToTest =  dp.computeSimilarities("similarities.txt", indexList);
        
        
        dp.printSimilarities(simToTest, 3);
        assertTrue(outContent.toString().contains("File1.txt File2.txt 3"));
        assertTrue(outContent.toString().contains("File1.txt File3.txt 3"));
        assertTrue(outContent.toString().contains("File3.txt File2.txt 3"));
        
    }
    
    @Test
    public void testProcessandStore() throws IOException {
        //use processStore
        List<Tuple<String, Integer>> indexList2 = dp.processAndStore(
                "test_files", "test.txt", 4);
        
        int l1 = 0;
        int l2 = 0;
        for (int i = 0; i < indexList2.size(); i++) {
            
            if (indexList2.get(i).getLeft().equals("File1.txt")) {
                l1 = indexList2.get(i).getRight();
            }
            if (indexList2.get(i).getLeft().equals("File2.txt")) {
                l2 = indexList2.get(i).getRight();
            }
            
        }
        assertEquals(l1, 28);
        assertEquals(l2, 42);
        

    }

}
