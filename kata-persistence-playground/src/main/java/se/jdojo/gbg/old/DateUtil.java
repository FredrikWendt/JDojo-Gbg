package se.jdojo.gbg.old;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public Date asDate(String string) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(string);
		} catch (ParseException e) {
			throw new RuntimeException("Date handling error");
		}
	}

}
