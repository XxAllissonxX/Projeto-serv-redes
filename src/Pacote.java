import java.io.Serializable;

public class Pacote implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public boolean isAck;
	public boolean isSyn;
	public int numeroSequencia;
	public int numeroAck;
	public int length;
	public String dados;
	
	
	
	public Pacote(boolean isSyn, boolean isAck, int numeroSequencia, int numeroAck,  int length, String dados) {
		this.isSyn = isSyn;
		this.isAck = isAck;
		this.numeroSequencia = numeroSequencia;
		this.numeroAck = numeroAck;
		this.length = length;
		this.dados = dados;
	}
	
	public Pacote(boolean isSyn, boolean isAck, int numeroSequencia, int numeroAck,  int length) {
		this.isSyn = isSyn;
		this.isAck = isAck;
		this.numeroSequencia = numeroSequencia;
		this.numeroAck = numeroAck;
		this.length = length;
		
	}
	
	@Override
	public String toString()
	{
		return "Pacote [isAck=" + isAck + ", isSyn=" + isSyn + ", seqNum=" + numeroSequencia + ", ackNum=" + numeroAck
				+ ", length=" + length + ", dados= " + dados + " ]";
	}
	

}
