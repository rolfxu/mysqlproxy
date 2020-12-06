import java.io.File;
import java.io.StringReader;
import java.text.ParseException;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxleap.mysqlproxy.parser.SqlParserImpl;
import com.maxleap.mysqlproxy.parser.expression.Expression;

public class Single {

	
	public static void main(String[] args) throws ParseException, Exception {
		for (int i = 0; i < 1; i++) {
//			String line =" select      row_number() over(partition by province_code order by vid) as num from (select * from abc)";
			String line = FileUtils.readFileToString(new File("D:\\mysqlproxy\\src\\test\\java\\overbypartition.sql"));
			StringReader stream = new StringReader(line);
			SqlParserImpl sqlParser = new SqlParserImpl(stream);
			Expression plainSelect = sqlParser.SqlStmt();
//			ObjectMapper om = new ObjectMapper();
//			System.out.println(om.writeValueAsString(plainSelect));
			System.out.println(plainSelect);
		}
	}
	
	public static String strip(String s, String startQuote, String endQuote,
			String escape) {
		if (startQuote != null) {
			assert endQuote != null;
			assert startQuote.length() == 1;
			assert endQuote.length() == 1;
			assert escape != null;
			assert s.startsWith(startQuote) && s.endsWith(endQuote) : s;
			s = s.substring(1, s.length() - 1).replace(escape, endQuote);
		}
		return s;
	}
}
