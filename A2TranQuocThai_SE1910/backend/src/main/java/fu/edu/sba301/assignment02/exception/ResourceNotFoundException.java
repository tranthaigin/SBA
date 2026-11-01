/**
 * @author Trần Quốc Thái
 * Class: SE1910
 * Student ID: CE190243
 */
package fu.edu.sba301.assignment02.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
