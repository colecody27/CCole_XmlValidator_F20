package xmlvalidator;

import static sbcc.Core.*;

import java.util.*;
import java.util.regex.*;

import static java.lang.System.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * 
 * @author your_name_here
 *
 */
public class Main {

	public static void main(String[] args) {
		String tagRegex = "\n?</?\\w+>";
		Pattern tagPattern = Pattern.compile(tagRegex);
		String input = "<Cody></bob></joe><tommy/><!-- This is a comment --><?xmlversion=\"1.0\" encoding=\"UTF-8\"?><sometag />";
		Matcher tagMatcher = tagPattern.matcher(input);
		while (tagMatcher.find()) {
			String tagString = input.substring(tagMatcher.start(), tagMatcher.end());
			int spaceIndex, specialIndex, keyIndex;
			if (tagString.charAt(1) != '/') {// Open tag
				spaceIndex = tagString.indexOf(" ");
				specialIndex = tagString.indexOf(">");
				if (spaceIndex != -1 && spaceIndex < specialIndex)
					keyIndex = spaceIndex;
				else
					keyIndex = specialIndex;
				println("Open tag: " + tagString.substring(1, tagString.length() - 1));
				println("Index: " + keyIndex);
				// String openTag = tagString.substring(0, tagString.length() - 1);
			} else {
				String closeTagName = tagString.substring(2, tagString.length() - 1);// 1
				println("Close tag: " + closeTagName);

			}
		}

	}
}
