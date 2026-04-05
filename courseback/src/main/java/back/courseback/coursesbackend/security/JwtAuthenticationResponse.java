package back.courseback.coursesbackend.security;

/*
 * JwtAuthenticationResponse is a Data Transfer Object (DTO).
 * 
 * DESIGN PURPOSE:
 * 1. Structure: It wraps the raw JWT string into a proper JSON object.
 *    Instead of returning just "eyJhbGci...", it returns:
 *    {
 *      "accessToken": "eyJhbGci..."
 *    }
 *    This is cleaner and easier for frontend clients to parse.
 * 
 * 2. Extensibility: If you later strictly need to return more data
 *    (like "tokenType": "Bearer" or "expiresIn": 3600), you can just
 *    add fields here without breaking the frontend's ability to read
 *    the object.
 * 
 * 3. Usage: In AuthController.java, we use:
 *    return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
 *    
 *    - 'new JwtAuthenticationResponse(jwt)' -> Create the object and SET the data.
 *    - 'ResponseEntity.ok(...)' -> Wrap it in an HTTP 200 OK response.
 *    - Spring Boot (Jackson) -> Automatically converts this object to JSON.
 */
public class JwtAuthenticationResponse {

    private String accessToken;

    /*
     * NO-ARGS CONSTRUCTOR (The Empty Constructor):
     * Required by Jackson (the library Spring uses to convert Java to JSON).
     * If Jackson ever needs to read a JSON payload and convert it BACK into 
     * this object, it MUST have an empty constructor to create a "blank template"
     * before using the setter methods to fill in the data.
     */
    public JwtAuthenticationResponse() {
    }

    /*
     * PARAMETERIZED CONSTRUCTOR (The Convenience Constructor):
     * This is used in your AuthController. It allows you to create the object
     * AND set the token in one single line:
     * return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
     * 
     * Without it, you would have to write 3 lines of code every time.
     */
    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    // Jackson needs this getter to read the value and put it in the JSON response.
    // JSON Key: "accessToken" (derived from getAccessToken)
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}