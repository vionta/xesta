package net.vionta.xml.xesta.bind.serialize;

/**
 * The linked list wrapper to hold references to the first and 
 * last ends of the list.
 */
public class LinkedNodeObjectList { 

	/**
	 * The first.
	 */
	private LinkedNodeObject first;
	/**
	 * The last, my everything ;)
	 */
	private LinkedNodeObject last;
	
	public LinkedNodeObjectList() {
		super();
	}

	public boolean isBeforeFirst(LinkedNodeObject checkedNodeObject) {
		return (first.isBefore(checkedNodeObject.getNode()));
	}

	public LinkedNodeObjectList(LinkedNodeObject first) {
		this.first  = first;
	}
	
	public LinkedNodeObject getFirst() {
		return first;
	}

	public void setFirst(LinkedNodeObject first) {
		this.first = first;
	}

	void insertNext(LinkedNodeObject item) {
		if (first == null ) this.first = item;
		else {
			this.last.setNext(item);
			this.last = this.last.getNext();
		}
	}

	boolean isEmpty() {
		return this.first==null;
	}
}
