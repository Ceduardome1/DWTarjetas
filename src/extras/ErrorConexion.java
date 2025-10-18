package extras;

public class ErrorConexion extends Exception {
private static final long serialVersionUID = 1L;
	private final datosConexion causante;
	private String msgEspecifico;
	
	public ErrorConexion( datosConexion causante, String msg ) {
		msgEspecifico = msg;
		this.causante = causante;
	}
	
	public datosConexion getCausante() {
		return causante;
	}
	
	public String toString() {
		return String.format("Host: %s.\n", causante.getHost()+":"+causante.getPuerto() )+
		msgEspecifico;
	}
	
}