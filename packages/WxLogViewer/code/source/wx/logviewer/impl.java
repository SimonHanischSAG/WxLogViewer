package wx.logviewer;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.util.JournalLogger;
import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.Server;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
// --- <<IS-END-IMPORTS>> ---

public final class impl

{
	// ---( internal utility methods )---

	final static impl _instance = new impl();

	static impl _newInstance() { return new impl(); }

	static impl _cast(Object o) { return (impl)o; }

	// ---( server methods )---




	public static final void checkFileExistence (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(checkFileExistence)>> ---
		// @sigtype java 3.5
		// [i] field:0:required filename
		// [o] field:0:required exists
		IDataCursor idc = pipeline.getCursor();
		String filename = IDataUtil.getString(idc, "filename");
		
		String fileExists = "false";
		if (filename == null)
			filename = "";
		if (filename.endsWith("\\") || filename.endsWith("/"))
			filename = filename.substring(0, filename.length() - 1);
		
		try {
			java.io.File f = new java.io.File(filename);
			if (f.exists())
				fileExists = "true";
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
		
		idc.insertAfter("exists", fileExists);
		
		idc.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void cleanupConstantlyTailing (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(cleanupConstantlyTailing)>> ---
		// @sigtype java 3.5
		// [i] field:0:required longPollingIntervalInSeconds
		IDataMap pipeMap = new IDataMap(pipeline);
		int longPollingIntervalInSeconds = Integer.valueOf(pipeMap.getAsString("longPollingIntervalInSeconds"));
		
		Set<Entry<String, OpenReader>> openReaders = openReaderMap.entrySet();
		long currentDate = System.currentTimeMillis();
		
		for (Entry<String, OpenReader> entry : openReaders) {
			OpenReader openReader = entry.getValue();
		
			if (openReader.lastModified.getTime() < currentDate - longPollingIntervalInSeconds * 1000 * 2) {
				try {
					debugLogInfo("Close constantlyTailing for " + openReader.file.getName());
					openReaderMap.remove(entry.getKey());
					openReader.bufferedReader.close();
				} catch (IOException e) {
					debugLogError(e.getLocalizedMessage());
				}
			}
		}
			
		// --- <<IS-END>> ---

                
	}



	public static final void getFileContent (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getFileContent)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required filename
		// [o] field:0:required content
		// This service get the content of a file in String
		//
		// Input: 	filename:	name of the file you want to write
		// Output:	content:	content of file in string
		//
		// ****************************************************************************
		
		IDataCursor idc = pipeline.getCursor();  
		String filename = IDataUtil.getString(idc, "filename");  
		if (filename == null) {
			filename = IDataUtil.getString(idc, "fileName");
			if (filename == null) {
				throw new NullPointerException("Missing parameter: filename");
			}
		}
	
		try {  
			java.io.File file = new java.io.File(filename);
			StringBuffer contents = new StringBuffer();
			BufferedReader input =  new BufferedReader(new FileReader(file));
			
			try {
				String line = null; 
			        while (( line = input.readLine()) != null){
			        	contents.append(line);
			        	contents.append(System.getProperty("line.separator"));
			        }
			} catch (IOException e) {  
				throw new ServiceException(e.getMessage());  
			} finally {
				input.close();
			}
			idc.insertAfter("content", contents.toString());
		} catch (Exception e) {  
			throw new ServiceException(e.getMessage());  
		} finally {
			idc.destroy();			
		}
	
		// --- <<IS-END>> ---

                
	}



	public static final void getLogfiles (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getLogfiles)>> ---
		// @sigtype java 3.5
		// [o] field:0:required logfolder
		// [o] record:1:required logfiles
		// [o] - field:0:required filename
		// [o] - field:0:required absolutePath
		// [o] - field:0:required relativePath
		// [o] - field:0:required size
		// [o] - field:0:required modificationDate
			
		File logDir = getLogsFolder();
		
		ArrayList<IData> files = new ArrayList<IData>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		SimpleDateFormat sortSdf = new SimpleDateFormat("yyyyMMddHHmmss");
		DecimalFormat df = new DecimalFormat("000000000");
		
		IOFileFilter ff = new AgeFileFilter(System.currentTimeMillis() - 30L * 84600 *  1000, false);
		Collection<File> logfiles = FileUtils.listFiles(logDir, ff, TrueFileFilter.INSTANCE);
		for (File logfile : logfiles) {
			if (!logfile.getAbsolutePath().contains("WxConfig") || !logfile.getAbsolutePath().contains("audit")) {
				IData iLogfile = IDataFactory.create();
				IDataCursor logfileCursor = iLogfile.getCursor();
				IDataUtil.put(logfileCursor, "filename", logfile.getName());
				IDataUtil.put(logfileCursor, "absolutePath", logfile.getAbsolutePath());
				
				String relPath = logfile.getAbsolutePath().substring(logDir.getAbsolutePath().length());
				IDataUtil.put(logfileCursor, "relativePath", relPath);
				
				IDataUtil.put(logfileCursor, "size", FileUtils.byteCountToDisplaySize(logfile.length()));
				IDataUtil.put(logfileCursor, "sortSize", df.format(logfile.length()));
				IDataUtil.put(logfileCursor, "modificationDate", sdf.format(logfile.lastModified()));
				IDataUtil.put(logfileCursor, "sortModificationDate", sortSdf.format(logfile.lastModified()));
				
				logfileCursor.destroy();
				files.add(iLogfile);
			}
		}
		
		IDataCursor pipelineCursor = pipeline.getCursor();
		IDataUtil.put(pipelineCursor, "logfolder", logDir.getAbsolutePath());
		IDataUtil.put(pipelineCursor, "folderSize", FileUtils.byteCountToDisplaySize(FileUtils.sizeOfDirectory(logDir)));
		
		IData[] logfilesArray = new IData[files.size()];
		files.toArray(logfilesArray);
		IDataUtil.put(pipelineCursor, "logfiles", logfilesArray);
		pipelineCursor.destroy();
			
			
			
			
		// --- <<IS-END>> ---

                
	}



	public static final void isStringInList (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(isStringInList)>> ---
		// @sigtype java 3.5
		// [i] field:1:required stringList
		// [i] field:0:required searchString
		// [o] field:0:required result {"true","false"}
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String[] stringList = IDataUtil.getStringArray(pipelineCursor, "stringList");
		String searchString = IDataUtil.getString(pipelineCursor, "searchString");
		pipelineCursor.destroy();
		
		String result = "false";
		
		if (stringList != null && stringList.length > 0) {
			for (int i = 0; i < stringList.length; i++)
				if (stringList[i] != null && stringList[i].equals(searchString)) {
					result = "true";
					break;
				}
		}
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put(pipelineCursor_1, "result", result);
		pipelineCursor_1.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void shutdownConstantlyTailing (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(shutdownConstantlyTailing)>> ---
		// @sigtype java 3.5
		Set<Entry<String, OpenReader>> openReaders = openReaderMap.entrySet();
		for (Entry<String, OpenReader> entry : openReaders) {
			OpenReader openReader = entry.getValue();
			try {
				openReader.bufferedReader.close();
			} catch (IOException e) {
				debugLogError(e.getLocalizedMessage());
			}
		}
		openReaders.clear();
			
		// --- <<IS-END>> ---

                
	}



	public static final void tailLogFileConstantly (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(tailLogFileConstantly)>> ---
		// @sigtype java 3.5
		// [i] field:0:required filename
		// [i] field:0:required clientId
		// [i] field:0:required initialSize
		// [i] field:0:required longPollingIntervalInSeconds
		// [o] field:1:required logdata
		IDataMap pipeMap = new IDataMap(pipeline);
		String filename = pipeMap.getAsString("filename");
		String clientId = pipeMap.getAsString("clientId");
		int initialSize = Integer.valueOf(pipeMap.getAsString("initialSize"));
		int longPollingIntervalInSeconds = Integer.valueOf(pipeMap.getAsString("longPollingIntervalInSeconds"));
		
		String key = clientId + filename;
		OpenReader openReader = openReaderMap.get(key);
		if (openReader == null) {
			// First access -> return last initialSize lines
			File file = new File(filename);
			FileReader fileReader = null;
			BufferedReader bufferedReader = null;
			try {
				fileReader = new FileReader(file);
				bufferedReader = new BufferedReader(fileReader);
				LinkedList<String> list = new LinkedList<String>();
				String line = null;
				do {
					line = bufferedReader.readLine();
					if (line != null) {
						list.add(line);
						if (list.size() > initialSize) {
							list.removeFirst();
						}
					} 
				} while (line != null);
				openReader = new OpenReader(file, file.length(), bufferedReader);
				openReaderMap.put(key, openReader);
				
				String[] logdata = new String[list.size()];
				int index = 0;
				for (String logline : list) {
					logdata[index] = logline;
					index++;
				}
				pipeMap.put("logdata", logdata);
			} catch (IOException e) {
				throw new ServiceException(e);
			}
		} else {
			// Further access -> return delta
			
			// Check for rollover:
			long currentFileLength = openReader.file.length();
			if (currentFileLength < openReader.lastFileLength) {
				// Rollover happened -> reset reader
				try {
					if (openReader.bufferedReader != null) {
						openReader.bufferedReader.close();
					}
				} catch (IOException e) {
					debugLogError(e.getMessage());
				}
				try {
					openReader.bufferedReader = new BufferedReader(new FileReader(openReader.file));
				} catch (FileNotFoundException e) {
					throw new ServiceException(e);
				}
			}
			
			String line = null;
			boolean lineRed = false;
			ArrayList<String> list = new ArrayList<String>(100);
			try {
				long breakAt = System.currentTimeMillis() + longPollingIntervalInSeconds * 1000;
				do {
					do {
						line = openReader.bufferedReader.readLine();
						if (line != null) {
							list.add(line);
							lineRed = true;
						}
					} while (line != null);
					if (!lineRed) {
						Thread.sleep(1000);
					}
				} while (!lineRed && System.currentTimeMillis() < breakAt);
				openReader.lastFileLength = openReader.file.length();
				openReader.lastModified = new Date();
				String[] logdata = list.toArray(new String[0]);
				pipeMap.put("logdata", logdata);
			} catch (IOException | InterruptedException e) {
				throw new ServiceException(e);
			}
		}
		// --- <<IS-END>> ---

                
	}



	public static final void throwError (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(throwError)>> ---
		// @sigtype java 3.5
		// [i] field:0:required msg
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		String	msg = IDataUtil.getString( pipelineCursor, "msg" );
		pipelineCursor.destroy();
		
		throw new ServiceException(msg);			
			
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	private static final String LOG_FUNCTION = "WxLog4j2";
	
	static ConcurrentHashMap<String, OpenReader> openReaderMap = new ConcurrentHashMap<String, OpenReader>();
	
	public static class OpenReader {
		public File file = null;
		public long lastFileLength = 0;
		public BufferedReader bufferedReader = null;
		public Date lastModified = new Date();
		public OpenReader(File file, long lastFileLength, BufferedReader bufferedReader) {
			this.file = file;
			this.lastFileLength = lastFileLength;
			this.bufferedReader = bufferedReader;
		}
		
	}
	
	public static final File getLogsFolder() throws ServiceException {
		File logsFolder = new File(Server.getLogDir(), "" );
		if (logsFolder.isDirectory()) {
			return logsFolder;
			} 
			else {
			
			
			throw new ServiceException("Cannot find logsFolder directory!");
		}
	}		
	
	private static void debugLogError(String message) {
	    JournalLogger.log(4,  JournalLogger.FAC_FLOW_SVC, JournalLogger.ERROR, LOG_FUNCTION, message);
	}
	
	private static void debugLogInfo(String message) {
	    JournalLogger.log(4,  JournalLogger.FAC_FLOW_SVC, JournalLogger.INFO, LOG_FUNCTION, message);
	}
	
	private static void debugLogDebug(String message) {
	    JournalLogger.log(4,  JournalLogger.FAC_FLOW_SVC, JournalLogger.INFO, LOG_FUNCTION, message);
	}
		
		
		
	// --- <<IS-END-SHARED>> ---
}

