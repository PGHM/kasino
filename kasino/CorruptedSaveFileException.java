package kasino;

// If the file that SaveLoad tries to parse ain't properly built or it's manipulated, this will be thrown

public class CorruptedSaveFileException extends Exception {
	// this is something eclipse warned about, so I added it, can't harm anything I guess
	private static final long serialVersionUID = 1L;

	public CorruptedSaveFileException (String message) {
        super(message);
    }
}
