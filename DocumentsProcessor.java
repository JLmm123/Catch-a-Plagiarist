import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class DocumentsProcessor {
    
    Map <String, List<String>> processDocuments(String directoryPath, int n) {
        File folder = new File(directoryPath);  
        File[] listOfFiles = folder.listFiles();
        Map<String, List<String>> docs = new HashMap<String, List<String>>();
        for (int i = 0; i < listOfFiles.length; i++) {
            //initialize reading 
            Reader r;
            try {
                r = new FileReader(directoryPath + File.separator 
                    + listOfFiles[i].getName());
                BufferedReader br = new BufferedReader(r);
                DocumentIterator di = new DocumentIterator(br, n);
                List<String> stringToPut = new ArrayList<String>();

                while (di.hasNext()) {
                    stringToPut.add(di.next());
                }

                docs.put(listOfFiles[i].getName(), stringToPut);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return docs;
    }


    List<Tuple<String, Integer>> storeNGrams(
            Map<String, List<String>> docs, String nwordFilePath) {
        //for every file in docs
        //create a tuple 
        List<Tuple<String, Integer>> list = new ArrayList<Tuple<String, Integer>>();

        File f = new File(nwordFilePath);
        try {
            f.createNewFile();
            FileWriter myWriter = new FileWriter(nwordFilePath);

            for (Map.Entry<String, List<String>> pair : docs.entrySet()) {
                int wordCount = 0;
                for (String nGrams: pair.getValue()) {
                    myWriter.write(nGrams + " ");
                    wordCount += (nGrams.length() + 1);
                }
                Tuple<String, Integer> tupleToPut = 
                        new Tuple<String, Integer>(pair.getKey(), wordCount);
                list.add(tupleToPut);
            }

            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list; 
    }

    public TreeSet<Similarities> computeSimilarities(
            String nwordFilePath, List<Tuple<String, Integer>> fileindex) {
    
//            Comparator<Similarities> comp = new Comparator<Similarities>() {
//                  @Override
//                    public int compare(Similarities o1, Similarities o2) {
//                    if (o1.getFile1().equals(o2.getFile1()) && 
//                    o1.getFile2().equals(o2.getFile2())) {
//                    return 0;
//                    }
//                    if (o1.getFile1().equals(o2.getFile2()) && 
//                    o1.getFile2().equals(o2.getFile1())) {
//                    return 0;
//                    } else {
//                    return 1;
//                    }
//                    }
//                    };

        TreeSet<Similarities> output = new TreeSet<Similarities>();
        Map<String, List <String>>occur = new HashMap <String, List <String>>();
            //create an iterator for indexList， using bufferedreader
        Reader r;
        try {
            r = new FileReader(nwordFilePath);
            BufferedReader br = new BufferedReader(r);
            String line;

            while ((line = br.readLine()) != null) {
                String [] words = line.split(" ");
                int totalLength = 0;
                int lengthOfFile = 0;
                int index = 0;
                for (int i = 0; i < words.length; i++) {
                    totalLength += (words[i].length() + 1);
                    // if this word belongs to the next file
                    if ((lengthOfFile + fileindex.get(index).getRight()) < totalLength) {
                        lengthOfFile += (fileindex.get(index).getRight());
                        index += 1;  
                    }
                    //add this word and this file to the map
                    if (occur.containsKey(words[i])) {
                        if ((!occur.get(words[i]).contains(fileindex.get(index).getLeft()))) {

                            //if this word appears before in the map, 
                            //add the filename to the list in the map
                            
                            for (int k = 0; k < occur.get(words[i]).size(); k++) {

                                String otherfile = occur.get(words[i]).get(k);
                                Similarities similarity =  new Similarities(
                                    otherfile, fileindex.get(index).getLeft());
                                if (!output.contains(similarity)) {
                                    similarity.setCount(1);
                                    
                                    output.add(similarity);
                                       
                                } else {
                                    Similarities s = output.ceiling(similarity);
                                    s.setCount(s.getCount() + 1);
                                       //output.add(s);
                                }
                            }
                            occur.get(words[i]).add(fileindex.get(index).getLeft());
                        }
                    } else {
                        //if this word not appear before in the map, 
                        //create a list of this word in the map and add the filename
                        List <String> fileAppeared = new ArrayList <String>();
                        fileAppeared.add(fileindex.get(index).getLeft());
                        
                        occur.put(words[i], fileAppeared);
                    }
                    
                }
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output; 
    }
    


    public void printSimilarities(TreeSet<Similarities> sims, int threshold) {

//        Comparator<Similarities> comp = new Comparator<Similarities>() {
//            @Override
//            public int compare(Similarities o1, Similarities o2) {
//                if (o1.getCount() == o2.getCount()) {
//                    return o1.getFile1().compareTo(o2.getFile1());
//                } else {
//                    return o2.getCount() - o1.getCount();
//                }
//
//            }
//        };
        SIMILARITIESCOMPARER sc = new SIMILARITIESCOMPARER();
        TreeSet<Similarities> simNew = new TreeSet<>(sc);
        simNew.addAll(sims);

        for (Similarities s: simNew) {
            if (s.getCount() >= threshold) {
                System.out.println(s.getFile1() + " " + s.getFile2() + " " + s.getCount());
            }
        }
        
    }
    
    List<Tuple<String, Integer>> processAndStore(
        String directoryPath, String sequenceFile, int n) {
        List<Tuple<String, Integer>> output = new ArrayList<Tuple<String, Integer>>();
        File folder = new File(directoryPath);  
        File[] listOfFiles = folder.listFiles();
        
        for (int i = 0; i < listOfFiles.length; i++) {
            //initialize reading 
            Reader r;
            try {
                r = new FileReader(directoryPath + File.separator + listOfFiles[i].getName());
                BufferedReader br = new BufferedReader(r);
                FileWriter myWriter;
                try {
                    myWriter = new FileWriter(sequenceFile);
                    DocumentIterator di = new DocumentIterator(br, n);
                    List<String> stringToPut = new ArrayList<String>();
                    int length = 0;
                    while (di.hasNext()) {
                        String next = di.next();
                        stringToPut.add(next + " ");
                        length += (next.length() + 1);
                    }
                    
                    Tuple <String, Integer> tuple = new Tuple <String, Integer>(
                            listOfFiles[i].getName(), length);
                    output.add(tuple);
                    for (String str: stringToPut) {
                        myWriter.write(str);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
             
                
                
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        return output;
    }
}
