package net.vionta.xml.xesta.exception;

/**
 * An exception thrown during the mapping process.
 */
public class MappingException extends BindingException {
	public MappingException() {}
	public MappingException(String message) { 
		this.setValue(message);
	}

	private String targetPropertyName;
	
	@Override
	public String toString() {
		return "Could not get a mapping [sourceClassName=" + sourceClassName + ", targetPropertyName=" + targetPropertyName
				+ ", value=" + value + ", mappingExpression=" + mappingExpression + ", exception=" +exception + "]";
	}


	public String getTargetPropertyName() {
		return targetPropertyName;
	}
	public void setTargetPropertyName(String targetClassName) {
		this.targetPropertyName = targetClassName;
	}

}
