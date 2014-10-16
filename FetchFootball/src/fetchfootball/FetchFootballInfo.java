/**
 * @description	获取网页信息并解析
 * example: java -cp ./FetchFootball.jar:./htmlunit-2.15-OSGi.jar fetchfootball.FetchFootballInfo http://info.win007.com/cn/League.aspx?SclassID=36
 *
 * @author  侯少龙
 * @date	2014-10-12
 */

package fetchfootball;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;

public class FetchFootballInfo {
	
	private static String directionName;
    
    public static void main(String[] args) {

        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
        try {
        	String pageUrlString = "http://info.win007.com/cn/League.aspx?SclassID=36";
        	if (args.length == 1 && args[0] != null && args[0].length() > 0) {
        		pageUrlString = args[0];
        	}
        	
            System.out.println("fetch page：" + pageUrlString);
        	
        	directionName = System.currentTimeMillis() + "";
        	FileWriteUtil.createDir(directionName);
			final HtmlPage scorePage = webClient.getPage(pageUrlString);
			fetchSuperFootballScore(scorePage);
			fetchAllScore(scorePage);

		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        webClient.closeAllWindows();
    }
    
    /**
     * 抓取超级联赛赛程积分
     * @param htmlPage
     */
    private static void fetchSuperFootballScore(HtmlPage htmlPage) {
		System.out.println("******fetch Super Football Score start*******"); 
        HtmlTable table = (HtmlTable)htmlPage.getElementById("Table2");
        int rowCount = 2;
        int colCount = 19;
        for (int row = 0; row < rowCount; row++) {
        	for (int col = 0; col < colCount; col++) {
                HtmlTableCell td;
        		if (row == 0) {
        			td = table.getCellAt(row, col+1);
        		} else {
        			td = table.getCellAt(row, col);
        		}
        		
        		try {
        			HtmlPage superFootballScorePage = td.click();
        			parseSuperEnglishFootballResult(superFootballScorePage, row * colCount + col + 1);
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        }
        
		System.out.println("******fetch Super Football Score end*******"); 
    }
    
    /**
     * 抓取总积分榜数据
     * @param htmlPage
     */
    private static void fetchAllScore(HtmlPage htmlPage) {
        System.out.println("******All football score start******"); 
        for (int i = 1; i < 7; i++) {
            String javaScriptCode = "SelectScore("+ i +");";
            ScriptResult result = htmlPage.executeJavaScript(javaScriptCode);
            HtmlPage scriptPage = (HtmlPage)result.getNewPage();
            parseAllResult(scriptPage, i);
        }
        System.out.println("******All football score end******"); 
    }

    /**
     * 解析超级联赛赛程积分
     * @param htmlPage
     */
	private static void parseSuperEnglishFootballResult(HtmlPage htmlPage, int index) {
        HtmlTable table = (HtmlTable)htmlPage.getElementById("Table3");
        int count = table.getRowCount();
        int colCount = 11;
        String[][] scoreLists = new String[count - 2][colCount];
        int i = 0;
        for (int row = 2; row < count; row++) {
        	for (int col = 0; col < colCount; col++) {
                HtmlTableCell td = table.getCellAt(row, col);
                String tdValue = td.asText();
                tdValue = tdValue.replace("\r\n", " ");
                tdValue = tdValue.replace("\n", " ");
    	        scoreLists[i][col] = tdValue;
        	}
        	i++;
        }
        
        String contentString = "轮次	时间	主队	比分	客队	(让球)全场	(让球)半场	(大小)全场	(大小)半场	资料	半场\r\n";
        for (int row = 0; row < count - 2; row++) {
        	for (int col = 0; col < colCount; col++) {
    	        if (col == colCount - 1) {
    	        	contentString += scoreLists[row][col];
    	        } else {
    	        	contentString += scoreLists[row][col] + "	";
    	        }
        	}
        	
        	contentString += "\r\n";
        }
        
        String fileName = directionName + "/赛程积分第" + index + "轮" + ".csv";
		try {
			FileWriteUtil.writeStringToFile(fileName, contentString);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * 解析总积分榜数据
	 * @param htmlPage
	 * @param index
	 */
	private static void parseAllResult(HtmlPage htmlPage, int index) {
        HtmlTable table = (HtmlTable)htmlPage.getElementById("div_Table1");
        int rowCount = table.getRowCount();
        int colCount = 15;
        if (index == 1) {
        	rowCount--;
        	colCount = 16;
        }
        
        String[][] scoreLists = new String[rowCount - 1][colCount];
        int i = 0;
        for (int row = 1; row < rowCount; row++) {
        	for (int col = 0; col < colCount; col++) {
                HtmlTableCell td = table.getCellAt(row, col); 
                String tdValue = td.asText();
                tdValue = tdValue.replace("\r\n", " ");
                tdValue = tdValue.replace("\n", " ");
    	        scoreLists[i][col] = tdValue;
        	}
        	i++;
        }
        
        String contentString = "排名	球队名称	赛	胜	平	负	得	失	净	胜%	平%	负%	均得	均失	积分\r\n";
        if (index == 1) {
        	contentString = "排名	球队名称	赛	胜	平	负	得	失	净	胜%	平%	负%	均得	均失	积分	近六轮\r\n";
        }
        for (int row = 0; row < rowCount - 1; row++) {
        	for (int col = 0; col < colCount; col++) {
    	        if (col == colCount - 1) {
    	        	contentString += scoreLists[row][col];
    	        } else {
    	        	contentString += scoreLists[row][col] + "	";
    	        }
        	}
        	
        	contentString += "\r\n";
        }
        
        String fileName = directionName + "/总积分第" + index + "列" + ".csv";
		try {
			FileWriteUtil.writeStringToFile(fileName, contentString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}