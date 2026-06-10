package net.vionta.xml.xesta.exception;

/**
 * An exception thrown while trying to bind a document 
 * and a class with the provided or extracted mapping 
 * information.
 */
public class BindingException extends Exception {

	/**
	 * The class name that wraps the binding relation.
	 */
	protected String sourceClassName;
	
	/**
	 * The class that should receive the binding 
	 * value.
	 */
	
	private String targetClassName;
	/**
	 * The value that was tried in the 
	 * binding process.
	 */
	
	protected String value;
	/**
	 * The mapping expression used in the 
	 * unsuccessful binding try.
	 */
	protected String mappingExpression;
	
	protected Exception exception;
	@Override
	public String toString() {
		return "Could not get a bind operation [sourceClassName=" + sourceClassName + ", targetClassName=" + targetClassName
				+ ", value=" + value + ", mappingExpression=" + mappingExpression + ", exception=" +exception + "]";
	}
	
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}

	public String getSourceClassName() {
		return sourceClassName;
	}

	public void setSourceClassName(String sourceClassName) {
		this.sourceClassName = sourceClassName;
	}

	public String getTargetClassName() {
		return targetClassName;
	}

	public void setTargetClassName(String targetClassName) {
		this.targetClassName = targetClassName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMappingExpression() {
		return mappingExpression;
	}

	public void setMappingExpression(String mappingExpression) {
		this.mappingExpression = mappingExpression;
	}
	
}
