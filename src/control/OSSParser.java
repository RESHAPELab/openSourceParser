package control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import DAO.FileDAO;
import model.API;
import model.UnitComp;
import util.MapUtil;


public class OSSParser {

	private Map <String, Integer> dictionary = new HashMap();
	private Map <String, String> complements = new HashMap();
	private List sources = new ArrayList();
	private List dirs = new ArrayList();
	private List<String> reservedWords = new ArrayList<String>();
	private OutputStream os = null;
	private OutputStreamWriter osw = null; 
    private BufferedWriter bw = null; 
	private OutputStream osf = null;
	private OutputStreamWriter oswf = null; 
    private BufferedWriter bwf = null; 
	private OutputStream osa = null;
	private OutputStreamWriter oswa = null; 
    private BufferedWriter bwa = null; 
	private OutputStream osfa = null;
	private OutputStreamWriter oswfa = null; 
    private BufferedWriter bwfa = null; 

    private List<String> counts = new ArrayList();  
    private FileDAO fd; 
	private String dirTrab = System.getProperty("user.dir");
	private String format = "java";
	private String db = "Y";
	private String csv ="Y";
	private String dbConnect = "dev";
	private String user = "postgres";
	private String pswd = "admin";
	private String project = "jabref";
	private String userSkills = "N";
	private String log = " ";
	private String outDir = " ";
	
	public OSSParser(String[] args) {
		// TODO Auto-generated constructor stub
		/*List of parameters:
		0: dirTrab ex: /Users/fabiomarcosdeabreusantos/Documents/dev/github/jabref/src
		1: format ex: java
		2: save in db ex: Y
		3: save in csv ex: Y
		4: db name to save ex: dev
		5: db user ex: postgres
		6: db password ex: admin
		7: project name ex: jabref
		8: outdir (csv) ex: private String log = " ";
		9: list of reserved words to search (blank separeted) ex: import
		*/
		/* arguments example java
		 * 
	 	/Users/fd252/Documents/dev/jabref-5.0-alpha
		java
		N
		Y
		devaugust2020
		postgres
		admin
		jabref50
		/Users/fd252/Documents/Cursos3/NAU/Research/JabRef/
		import
		
		arguments example cpp (h files)
		
		"/Volumes/GoogleDrive/My Drive/dev/audacity-master"
		h
		N
		Y
		teste
		postgres
		admin
		audacity
		/Users/fd252/OneDrive/Production/ETL1-Pipeline-main/data/outputs/new/audacity/
		#include
		
		Using the jar:
		
		java -jar OSSParser.jar "/Volumes/GoogleDrive/My Drive/dev/audacity-master" h N Y teste postgresql admin audacity /Users/fd252/OneDrive/Production/ETL1-Pipeline-main/data/outputs/new/audacity/ #include

		 **/

		dirTrab=args[0];
		format = args[1];
		db = args[2];
		csv = args[3];
		dbConnect = args[4];
		user = args[5];
		pswd = args[6];
		project = args[7];
		outDir = args[8];
		
		System.out.println("Starting with following parameters:");
		System.out.println("-----------------------------------");
		System.out.println("Dir Trab: " + dirTrab);
		System.out.println("Language: " + format);
		System.out.println("Generate DB: " + db);
		System.out.println("Generate CSV: " + csv);
		
		if (args[9]==null){
			System.out.println("Generating a dictionary ");
		}
		for (int i=9; i<args.length; i++){
			reservedWords.add(args[i]);
			System.out.println("Looking for: " + args[i]);
		}
		
//		userSkills = args[9];
//		if (args[9].equals("N")){
//			System.out.println("Parsing Project ");
//		}
//		else {
//			System.out.println("Parsing User File");
//			parseUserFile(args[10]);
//		}
		
		System.out.println("-----------------------------------");
	
		//JFileChooser fileChooser = new JFileChooser();
		//fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		//File selectedFile = new File("C:\\\\temp\\\\ALIN-cmt-confOf.rdf");
		/*int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
		    selectedFile = fileChooser.getSelectedFile();
		    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		}
		*/
		dirs.add(dirTrab);
		if (db.equals("Y")) {

			fd = FileDAO.getInstancia(dbConnect, user, pswd);
			log += fd.insertProject(project, dirTrab, format);
		}
		try {
			os = new FileOutputStream(dirTrab + "JabRefImports.csv");
			osw = new OutputStreamWriter(os);
			bw = new BufferedWriter(osw);
	    	bw.write("File,Word,Complement \n");
	    	
	    	if (csv.equals("Y" )){
	    		
    			osf = new FileOutputStream(outDir+project+"_file.csv");
    			osa = new FileOutputStream(outDir+project+"_api.csv");
    			osfa = new FileOutputStream(outDir+project+"_fileapi.csv");
    			System.out.println("Writing " + outDir+project+"_file.csv");
    			System.out.println("Writing " + outDir+project+"_api.csv");
    			System.out.println("Writing " + outDir+project+"_fileapi.csv");

    			oswf = new OutputStreamWriter(osf);
    			oswa= new OutputStreamWriter(osa);
    			oswfa = new OutputStreamWriter(osfa);
    			bwf = new BufferedWriter(oswf);
    			bwa = new BufferedWriter(oswa);
    			bwfa = new BufferedWriter(oswfa);

    			bwf.write("file_name,full_name,project \n");
    			bwa.write("api_name,class,count,expert \n");
    			bwfa.write("file_name,api_name,count \n");
	    	}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Starting----------------");			
		
		for (int i=0; i<dirs.size(); i++) {
			processDir(dirs.get(i));	
		}
		try {
			bw.close();
			if (csv.equals("Y" )){
				bwa.close();
				bwf.close();
				bwfa.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (csv.equals("Y"))
			printCounts();
		else {
			printDictionary(dictionary, "dict");	    
			printDictionary(complements, "complement");
		}
		
		writeLog(log);
		System.out.println("Finished----------------");			

	    
	}


	private void writeLog(String log2) {
		// TODO Auto-generated method stub
		try {
			os = new FileOutputStream(dirTrab+"/log.txt");
			osw = new OutputStreamWriter(os);
			bw = new BufferedWriter(osw);
	    	bw.write(log);
	    	bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private void parseUserFile(String string) {
		// TODO Auto-generated method stub
		System.out.println("Read csv");
		
		System.out.println("Populating Collection with Author, File_Name and Patch");
		
		System.out.println("Parsing File_name in n Files");
		
		System.out.println("Processing each File");
		
	}


	private void printCounts() {
		// TODO Auto-generated method stub
		
		try {
			os = new FileOutputStream(dirTrab+"/JabRefCounts.csv");
			osw = new OutputStreamWriter(os);
			bw = new BufferedWriter(osw);
	    	bw.write("File,FullName,Class,RefCount \n");
	    	for (String line: counts){
	    		bw.write(line);
	    	}
	    	bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private void printDictionary(Map dict, String label) {
		// TODO Auto-generated method stub
		//Map<String, Integer> treeMap = new TreeMap<String, Integer>(dictionary);
		Map sortedMap = MapUtil.sortByValue(dict);
		
		sortedMap.forEach((k,v)->System.out.println(label + k + " # : " + v));
	}


	private void processDir(Object obj) {
		// TODO Auto-generated method stub
		String dir = (String) obj;
		System.out.println("Processing ... "+dir);
		File folder = new File(dir);
		String source; 
		//processFile(selectedFile);
		File[] listOfFiles = folder.listFiles();
		ArrayList<File> files = new ArrayList<>();
		
	    for (int i = 0; i < listOfFiles.length; i++) {
	    	if (listOfFiles[i].isFile()) {
		        System.out.println("File " + listOfFiles[i].getName());
		        files.add(listOfFiles[i]);
		        source = processFile(listOfFiles[i], dir);
		        
		        sources.add(source);
		        boolean isNew;
		        if (sources!=null) {
		        	/*isNew = alinhamentos.add(alignment);
		        	if (!isNew) {
		        		System.out.println("Alinhamento igual:"+ listOfFiles[i].getName());
		        		alinhamentosDuplicate.add(alignment);
		        	}
		        	
		        	boolean found = false;
		        	for (Iterator i1 = alinhamentos.iterator(); i1.hasNext(); ) {
		        		Alignment alinAux = (Alignment) i1.next();
		        		if (alinAux.getOntologia1().getName() .equals( alignment.getOntologia1().getName()) && alinAux.getOntologia2().getName().equals( alignment.getOntologia2().getName())) {
		        			found = true;
		        		}
		        	}
		        	if (!found) {
		        		alinhamentos.add(alignment);
		        	} else {
		        		alinhamentosDuplicate.add(alignment);
		        	} */
		        }
		    } else if (listOfFiles[i].isDirectory()) {
		        System.out.println("Directory " + listOfFiles[i].getName());
		        dirs.add(listOfFiles[i].getAbsolutePath());
		    }
	    }
	}

	private String processFile(File selectedFile, String dir) {
		// TODO Auto-generated method stub
		ArrayList<String> newComplements = new ArrayList();
	    System.out.println("Arquivo da vez - "+selectedFile.getName());
	    
		if (selectedFile.getName().endsWith(format)){
			InputStream is = null;
			try {
				is = new FileInputStream(selectedFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    InputStreamReader isr = new InputStreamReader(is);
		    BufferedReader br = new BufferedReader(isr);
		    
		    String s = ""; 
		    String token = null;
		    StringTokenizer stk = null;
		    String source = null;
		    int pos = 0;
			try {

				//Stream st = br.lines(); 
				//st.
				System.out.println("leitura do file "+selectedFile.getName()+ " dir: "+dir);
				UnitComp uc = new UnitComp();
				uc.setName(selectedFile.getName());
				uc.setDir(dir);
				if (db.equals("Y")){
				
					log += fd.insertFile(uc, project);
				}
				if (csv.equals("Y")){

					bwf.write(uc.getName()+","+uc.getDir()+"/"+uc.getName()+","+project+"\n");
				}

				s = br.readLine();// primeira linha do arquivo
				source = s;
				//System.out.println(s);
				while (s != null) {
					
					int length = s.length();
					String word = null;
					String complement = null;
					//token = stk.nextToken();
					if (reservedWords.size()==0){
						// generating a dictionaty only
						pos = s.indexOf(" ");
						int i = 0;
						while (i<s.length()&&pos!=-1) {
							
							word = s.substring(i, pos);
							
							insertDictonary(word, dictionary);
							
							i = pos;
							pos = s.indexOf(" ", pos+1);
							
						}
						if (i+1<s.length()) {
							word = s.substring(i+1, s.length()-1);
							insertDictonary(word, dictionary);
						}
					} else {
						// parsing reserved words...
						pos = s.indexOf(reservedWords.get(0));
						int i = 0;
						if (pos!=-1) {
							
							word = reservedWords.get(0);
							
							//insertComplement(word, complement, complements);
							
							i = pos+word.length();
							
							if (i+1<s.length()-1){
								if (format.equals("java")||format.equals("cs"))
									complement = s.substring(i+1, s.length()-1); // change to s.length()?
								if (format.equals("cpp")||format.equals("h")) {
									complement = s.substring(i+1, s.length());
								    int tag = complement.lastIndexOf(">");
								    if (tag != -1) {
								    	complement = complement.substring(0, tag);
								    } else {
								    	complement = complement.substring(0, complement.length());
								    }
									complement = complement.replaceAll("\"","");
									complement = complement.replaceAll("<","");
									complement = complement.replaceAll(">","");

								}
								if (testComplement(complement)){

									insertComplement(selectedFile.getName(), word, complement, complements);
									System.out.println("processing:"+word+" "+complement);

									if (complement.equals("r.actions")) {
										System.out.println("debug:"+word+" "+complement);

										
									}
									if (complement.startsWith("static ")) {
										complement = complement.replace("static ","");
									}
									newComplements.add(complement);
									API api = new API();
									api.setFullName(complement); // add "\"?
									
									String searchString = "";
									if (format.equals("java")||format.equals("cs"))
										searchString = this.getSearchUnit(complement);
									if (format.equals("cpp")||format.equals("h"))
										searchString = this.getSearchUnitCpp(complement);
									
									searchString = searchString.replaceAll("<","");
									searchString = searchString.trim();

									api.setClassName(searchString);
									if (db.equals("Y")){
										log += fd.insertAPI(api);
										log += fd.insertFileAPI(uc, api);
									}
									if (csv.equals("Y")){
										bw.write(selectedFile.getName()+","+word+","+complement+"\n");
										bwa.write(api.getFullName()+","+api.getClassName()+",0, \n");
										bwfa.write(uc.getDir()+"/"+uc.getName()+","+api.getFullName()+"\n");

									}

								}
							}
							
						}
						
					}
					s = br.readLine();
					source = source + s;
					//System.out.println(s);
				}	    
				
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchElementException en) {
				en.printStackTrace();
			
			}
			
			countComplements(selectedFile.getName(), source, newComplements);
			
			return source;
		}
		return null;
	}



	private void countComplements(String name, String source, ArrayList<String> newComplements) {
		// TODO Auto-generated method stub
		
		if (format.equals("java")||format.equals("cs")) {
			for (String unit: newComplements){
				int countSearch = 0;
				unit = unit.trim();
				if (unit.lastIndexOf(".")!=-1) {
					String searchUnit = getSearchUnit(unit);
					if (searchUnit!=null){
						int pos = 0;
						pos = source.indexOf(searchUnit);
						while (pos!=-1){
							countSearch++;
							pos = source.indexOf(searchUnit,pos+1);
						}
						counts.add(name+","+unit+","+searchUnit+","+countSearch+"\n");
						if (db.equals("Y")){
							API api = new API();
							api.setFullName(unit);
							UnitComp uc = new UnitComp();
							uc.setName(name);
							log += fd.updateFileAPICount(uc, api, countSearch);
						}
					}
				} 
				
				
			}
		}
	}


	private String getSearchUnit(String unit) {
		// TODO Auto-generated method stub
		int dotPos = unit.lastIndexOf(".")+1;
		String searchUnit = unit.substring(dotPos,unit.length());
		Character c = searchUnit.charAt(0);
		if (!Character.isUpperCase(c)){
			String newUnit = unit.substring(0, dotPos-1);
			searchUnit = unit.substring(newUnit.lastIndexOf(".")+1, newUnit.length());
		}
		if (searchUnit!=null&&searchUnit.length()>0){
			return searchUnit;
		}
		return " "; //null?
	}

	private String getSearchUnitCpp(String unit) {
		// TODO Auto-generated method stub
		int ltPos = unit.lastIndexOf('/')+1;
		int gtPos = unit.lastIndexOf(">")+1;
		
		String searchUnit = "";
		if (ltPos == 0)
			if (gtPos == 0)
				searchUnit = unit;
			else
				searchUnit = unit.substring(ltPos,gtPos);
		else
			if (gtPos == 0)
				searchUnit = unit.substring(ltPos,unit.length());
			else
				searchUnit = unit.substring(ltPos,gtPos);

		if (searchUnit!=null){
			return searchUnit;
		}
		return null;
	}


	private boolean testComplement(String complement) {
		// TODO Auto-generated method stub
		if (format.equals("java")||format.equals("cs")){

			if (complement.indexOf(")")!=-1)
				return false;
			if (complement.indexOf("(")!=-1)
				return false;
			if (complement.indexOf("=")!=-1)
				return false;
			if (complement.indexOf(",")!=-1)
				return false;
			if (complement.indexOf(".")==-1)
				return false;
			return true;
		}
		return true;

	}


	private void insertDictonary(String word, Map dict) {
		// TODO Auto-generated method stub
		word = word.trim();
		if (word.lastIndexOf(";")!=-1) {
			word = word.substring(0,word.length()-1);
		}
		if (dict.containsKey(word)) {
			Integer value = (Integer) dict.get(word);
			value ++;
			dict.remove(word);
			dict.put(word, value);
			
		} else {
			dict.put(word, 1);
		}
	}
	
	private void insertComplement(String fileName, String word, String complement,  Map dict) {
		// TODO Auto-generated method stub
		word = word.trim();
		if (word.lastIndexOf(";")!=-1) {
			word = word.substring(0,word.length()-1);
		}
		
		dict.put(fileName + "-" + word + "-" + complement, complement);
		
	}

}
