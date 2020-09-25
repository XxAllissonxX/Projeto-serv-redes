import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.net.*;
import java.io.*;

public class Servidor {
	
	public final static double LOSS_RATE = 0.5; // Taxa de Perda
	public final static int PORTA = 52000; // Porta de comunicação do servidor
	public final static int TIMEOUT = 10; // Tempo de esgotamento 10 segundos
	public final static String ENDERECO_IP = "127.0.0.1"; // IP de loopback que aponta para a própria máquina
	public static Scanner sc = new Scanner(System.in);
	//public static String dados = sc.next();
	
	
	public static void main(String[] args) throws Exception{
		
		ArrayList<Pacote> pacotes = new ArrayList(); // ArrayList para reter os pacotes recebidos do cliente
		int receptorSeqNum = 1000; // inicia com esse valor o número de sequencia
		int ackAtual = -1;
		
		Random r = new Random(0);
		final Rede rede = new Rede(r, LOSS_RATE);
		final DatagramSocket socket = new DatagramSocket(53000);
		
		Pacote syn = rede.receber(socket); // Esperando pelo syn
		
		if(syn.isSyn == true && syn.isAck == false) {
			System.out.println("Syn recebido");
			ackAtual = syn.numeroSequencia + 1; //Primeiro ack
			Pacote synACK = new Pacote(true, true, receptorSeqNum, ackAtual, 1);
			rede.enviar(socket, ENDERECO_IP, PORTA, synACK);
			System.out.println("SYN ACK enviado");
			System.out.println();
			
			
			
			
			
		}
		
		while(true) {
			Pacote p = rede.receber(socket);
			
			if(p != null) {
				System.out.println("Recebido " + p);
				for(int i = 0; i < pacotes.size(); ++i) {
					System.out.println(pacotes.get(i));
				}
				
				System.out.println("ACK atual " + ackAtual );
				System.out.println();
				
			
				if(p.numeroSequencia < ackAtual) {
					Pacote pacote = new Pacote(false, true, receptorSeqNum + 1, ackAtual, 0); // ACK atual
					rede.enviar(socket, ENDERECO_IP, PORTA, pacote);
					System.out.println("ack enviado novamente " + ackAtual);
				}else {
					int ultimoAck = ackAtual;
					
					//Procurando por duplicações de pacotes
					boolean addSeg = true;

					for(int i = 0; i < pacotes.size(); ++i) {
						if (p.numeroSequencia == pacotes.get(i).numeroSequencia) {
							addSeg = false;
							break;
						}
					}

					// Add pacote se ele não estiver duplicado
					if(addSeg) {
						pacotes.add(p);// adicionando no pacote ao arrayList
					}


					//Verifique se há pacotes reconhecidos
					while (true) {
						Pacote x = null;
						for (int i = 0; i < pacotes.size(); ++i) {
							if (pacotes.get(i).numeroSequencia == ackAtual) {
								x = pacotes.get(i);
								break;
							}
						}
						if (x == null)
							break;
						else {
							ackAtual = x.numeroSequencia + x.length;
							pacotes.remove(x);
						}

					}


					// verificação se a confirmação não foi atualizada
					if (ultimoAck != ackAtual) {
						Pacote pacote = new Pacote(false, true, receptorSeqNum + 1, ackAtual, 0); //ACK
						rede.enviar(socket, ENDERECO_IP, PORTA, pacote);
						System.out.println("Ack Enviado: " + ackAtual + "\n");
					}


					for (int i = 0; i < pacotes.size(); ++i) {
						System.out.println(pacotes.get(i));
					}
					System.out.println("Ack Atual: " + ackAtual);
					System.out.println();
				}


			}
		}

		//ssocket.close();
	}
				
	

}
