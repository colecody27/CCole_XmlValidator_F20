package xmlvalidator;

import static sbcc.Core.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class BasicXmlValidator implements XmlValidator {

	@Override
	public List<String> validate(String xmlDocument) {
		// Create array list, stack and read file.
		List<String> errors = new ArrayList<String>();
		BasicXmlTagStack stack = new BasicXmlTagStack();

		// CREATE TAG PATTERN
		String tagRegex = "</?\\w+[^>]*>";
		Pattern tagPattern = Pattern.compile(tagRegex);
		Matcher tagMatcher = tagPattern.matcher(xmlDocument);

		// LINE COUNTER
		int lineCount = 1, sLineMatching = 0;
		String lineRegex = "\r\n";
		Pattern linePattern = Pattern.compile(lineRegex);

		// ENTER LOOP
		while (tagMatcher.find()) {
			String tagString = xmlDocument.substring(tagMatcher.start(), tagMatcher.end());
			String lineCountStr;
			lineCountStr = xmlDocument.substring(sLineMatching, tagMatcher.start());
			Matcher lineMatcher = linePattern.matcher(lineCountStr);

			// COUNT NEW LINES
			while (lineMatcher.find()) {
				lineCount++;
			}

			// MISSING QUOTES
			if (attributeError(errors, tagString, lineCount) != null)
				return attributeError(errors, tagString, lineCount);

			// CHANGE STARTING INDEX OF SUBSTRING TO THE START OF THIS TAG
			sLineMatching = tagMatcher.start();

			// GET TAG NAME AND PUSH OPEN TAG TO STACK
			if (tagString.charAt(1) != '/') {
				int endIndex = findTagName(tagString);
				XmlTag openTag = new XmlTag(tagString.substring(1, endIndex), lineCount);
				stack.push(openTag);
			} else {
				// COMPARE CLOSE TAG TO TOP OF STACK
				// IF STACK IS EMPTY RETURN ERROR 1
				String closeTagName = tagString.substring(2, tagString.length() - 1);// inclusive index's
				if (stack.getCount() == 0)
					return orphanError(errors, closeTagName, lineCount);// ERROR 1: OrphanTag
				XmlTag top = stack.peek(0);

				// COMPARE STRINGS
				if (closeTagName.equals(top.name))
					stack.pop();// IF EQUAL, POP TOP
				else
					return mismatchError(errors, top.name, top.index, closeTagName, lineCount);// ERROR 3: Tag mismatch
			}
		}
		if (stack.getCount() != 0)
			return unclosedError(errors, stack.peek(0).name, stack.peek(0).index);// ERROR 2: Unclsed tag
		return null;
	}


	// Isolates open tag name
	public int findTagName(String Opentag) {
		int index = 0, spaceKey, greaterKey;
		spaceKey = Opentag.indexOf(" ");
		greaterKey = Opentag.indexOf(">");
		if (spaceKey != -1 && spaceKey < greaterKey)
			index = spaceKey;
		else
			index = greaterKey;
		return index;
	}


	// ERROR #1
	public List<String> orphanError(List<String> errors, String tagString, int line) {
		errors.add("Orphan closing tag");
		errors.add(tagString);
		errors.add(Integer.toString(line));// ---Might need to confirm line number---
		return errors;
	}


	// ERROR #2
	public List<String> unclosedError(List<String> errors, String stackTop, int line) {
		errors.add("Unclosed tag at end");
		errors.add(stackTop);
		errors.add(Integer.toString(line));// ---Might need to confirm line number---
		return errors;
	}


	// ERROR #3
	public List<String> mismatchError(List<String> errors, String openTag, int openLine, String closeTag,
			int closeLine) {
		errors.add("Tag mismatch");
		errors.add(openTag);
		errors.add(Integer.toString(openLine));
		errors.add(closeTag);
		errors.add(Integer.toString(closeLine));
		return errors;
	}


	// FIND IF THERE IS A MISSING QUOTE
	public boolean isAttributeError(String tagName) {
		boolean error = false;
		int count = 0;
		// ATTRIBUTE REGEX SETUP
		String attRegex = "\"";
		Pattern attPattern = Pattern.compile(attRegex);
		Matcher attMatcher = attPattern.matcher(tagName);
		// COUNT QUOTES
		while (attMatcher.find()) {
			count++;
		}
		if (count == 1)
			error = true;
		return error;
	}


	// ATTRIBUTE ERROR || FIND ATTNAME & LINE NUMBER
	public List<String> attributeError(List<String> errors, String tagName, int line) {
		String attName = null;
		int attLine = line;
		// ATTRIBUTE NAME REGEX
		String attRegex = "\s\\w+=";
		Pattern attPattern = Pattern.compile(attRegex);
		Matcher attMatcher = attPattern.matcher(tagName);
		// LINE REGEX
		String lineRegex = "\r\n";
		Pattern linePattern = Pattern.compile(lineRegex);

		if (attMatcher.find() && isAttributeError(tagName)) {
			attName = tagName.substring(attMatcher.start() + 1, attMatcher.end() - 1);
			Matcher lineMatcher = linePattern.matcher(tagName.substring(0, attMatcher.end() + 1));
			while (lineMatcher.find()) {
				attLine++;
			}
		} else
			return null;
		errors.add("Attribute not quoted");
		errors.add(tagName);
		errors.add(Integer.toString(line));
		errors.add(attName);
		errors.add(Integer.toString(attLine));
		return errors;
	}

}
