package audiolibrary.exceptions;

public class SongAlreadyExistsException extends Exception{
    public SongAlreadyExistsException(){
        super("This song is already part of the library!");
    }
}
