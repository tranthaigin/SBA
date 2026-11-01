/**
 * @author Trần Quốc Thái
 * Class: SE1910
 * Student ID: CE190243
 */
package fu.edu.sba301.assignment02.exception;

public class BusinessRuleViolationException extends RuntimeException {
    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
