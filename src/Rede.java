import java.util.Random;



import java.net.*;
import java.io.*;

public class Rede {
	
	private Random r;
	private double loss_Rate;
	
	// O construtor da classe rede
	
	public Rede(Random r, double loss_Rate) {
		
		this.r = r;
		this.loss_Rate = loss_Rate;
	}
	
	public void enviar(DatagramSocket socket, String hostname, int portaDestino, Pacote pacote) throws Exception {
		
		// Deixa o pacote passar se for um syn ou syn+ack
		
		if(pacote.isSyn || r.nextDouble() > loss_Rate) {
			InetAddress endereco = InetAddress.getByName(hostname);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
			ObjectOutputStream os  = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			os.flush();
			os.writeObject(pacote);
			os.flush();
			byte[] envioBuffer = byteStream.toByteArray();
			DatagramPacket packet = new DatagramPacket(envioBuffer, envioBuffer.length, endereco, portaDestino);
			System.out.println("Enviando: " + pacote);
			socket.send(packet);
			os.close();
		}else {
			System.out.println("Perdido " + pacote);
		}
		
		
	}
	
	public void enviar(DatagramSocket socket, String hostname, int portaDestino, Pacote... pacotes) throws Exception {
		
		permuta(pacotes);
			for(Pacote p : pacotes) {
				enviar(socket, hostname, portaDestino, p);
				
			}
			
		
	}
	
	public Pacote receber(DatagramSocket socket) throws Exception {
		byte[] recebeBuffer = new byte[5000];
		DatagramPacket packet = new DatagramPacket(recebeBuffer, recebeBuffer.length);
		try {
			socket.receive(packet);
		} catch(SocketTimeoutException e) {
			return null;
		}
		
		ByteArrayInputStream byteStream = new ByteArrayInputStream(recebeBuffer);
		ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
		Pacote o = (Pacote) is.readObject();
		is.close();
		return (o);
		
	}
	
	public void permuta(Pacote[] pacotes) {
		
		for (int i = pacotes.length; --i > 0;)
		{
			int pos = r.nextInt(i);
			Pacote temp = pacotes[pos];
			pacotes[pos] = pacotes[i];
			pacotes[i] = temp;
		}
		
	}
	
	
	

}
