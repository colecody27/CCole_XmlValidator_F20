package xmlvalidator;

import static java.lang.System.*;

public class BasicXmlTagStack implements XmlTagStack {
	int count;
	XmlTag[] tags;

	public BasicXmlTagStack() {
		count = 0;
		tags = new XmlTag[count];
	}


	@Override
	public void push(XmlTag item) {
		// If there is no room, copy items into a larger array
		if (count == tags.length) {
			int newLength = tags.length * 2 + 1;
			XmlTag[] tempArr = new XmlTag[newLength];
			arraycopy(tags, 0, tempArr, 0, tags.length);
			tags = tempArr;
		}
		// Insert new tag into stack
		tags[count++] = item;
	}


	@Override
	public XmlTag pop() {
		if (count == 0)
			return null;
		else
			return tags[--count];
	}


	@Override
	public XmlTag peek(int position) {
		if (position > tags.length || position < 0)
			return null;
		else
			return tags[count - position - 1];// Why have a position?

	}


	@Override
	public int getCount() {
		return count;
	}

}
