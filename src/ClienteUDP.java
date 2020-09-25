import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;


public class ClienteUDP {
	
	public final static double LOSS_RATE = 0.2;
    public final static int PORTA_DEST = 53000;
    public final static int TIMEOUT = 1;
    public final static String ENDERECO_IP = "127.0.0.1";
    public static int envioSeqNum = 2000;//numero de sequencia enviado
    public static Scanner sc = new Scanner(System.in);
	//public static String dados = sc.next();


	
	public static void main(String[] args) throws Exception
	{
		//time iniciado do zero
		int time = 0;

		//HashMap, pacotes enviados e preenchidos e seus tempos limite correspondentes
		HashMap<Pacote, Integer> pacoteEnviado = new HashMap();

		Random r = new Random(0);

		//Objeto Rede criado está como final para não ser criado outro e assim perder a referência
		final Rede rede = new Rede(r, LOSS_RATE);
		//configuração socket
		final DatagramSocket socket = new DatagramSocket(52000);

		Pacote pacote = new Pacote(true, false, envioSeqNum, 0, 1, ""); //SYN
		// Envio do SYN
		rede.enviar(socket, ENDERECO_IP, PORTA_DEST, pacote);
		System.out.println("SYN Enviado");

		// esperando pelo SYN ACK
		Pacote synAck = rede.receber(socket);

		System.out.println("SYN ACK Recebido");

		updateSeqNum(1);// atualização do número de sequencia mais ack

		// Gerando tamanhos aleatorios para os pacotes
		int [] lengths = new int [10];
		for(int i = 0; i < lengths.length; ++i)
			lengths[i] = r.nextInt(50);

		// Create a bunch of segments
		Pacote [] pacotes = new Pacote [] {
				new Pacote(false, false, envioSeqNum, 1, lengths[0]),
				new Pacote(false, false, updateSeqNum(lengths[0]), 1, lengths[1]),
				new Pacote(false, false, updateSeqNum(lengths[1]), 1, lengths[2]),
				new Pacote(false, false, updateSeqNum(lengths[2]), 1, lengths[3]),
				new Pacote(false, false, updateSeqNum(lengths[3]), 1, lengths[4]),
				new Pacote(false, false, updateSeqNum(lengths[4]), 1, lengths[5]),
				new Pacote(false, false, updateSeqNum(lengths[5]), 1, lengths[6]),
				new Pacote(false, false, updateSeqNum(lengths[6]), 1, lengths[7]),
				new Pacote(false, false, updateSeqNum(lengths[7]), 1, lengths[8]),
				new Pacote(false, false, updateSeqNum(lengths[8]), 1, lengths[9]),
		};

		// Adicionando pacotes para o HashMap, com o valor do time
		for(int i = 0; i < pacotes.length; ++i){
			pacoteEnviado.put(pacotes[i], 100);
		}

		// Enviando pacotes
		rede.enviar(socket, ENDERECO_IP, PORTA_DEST, pacotes);

		// configurando o tempo de esgotamento do socket
		socket.setSoTimeout(TIMEOUT);

		while (true)
		{

			Pacote p = rede.receber(socket);

			if (p != null) {

				//memória do numero ack
				int ackFromReceiver = p.numeroAck;

				//verificação do ack
				if(p.isSyn == false && p.isAck == true) {

					//aceitando novas confirmações e excluindo pacotes que foram reconhecidos
					Iterator<Map.Entry<Pacote, Integer>> it = pacoteEnviado.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<Pacote, Integer> pair = it.next();

						Pacote key = pair.getKey();

						if (key.numeroSequencia < ackFromReceiver) {
							it.remove();
							System.out.println(key + " confirmação!");
						}
					}

				}
			}

			else{
				//envie tudo que não for reconhecido
				System.out.println("Insira uma mensagem");
				Pacote p1 = new Pacote(false, false, updateSeqNum(lengths[8]), 1, lengths[9],sc.nextLine());
				
				time += TIMEOUT;//atualizando o time
				if(time <= 3) {
					System.out.println("Tentando retransmitir");
					rede.enviar(socket, ENDERECO_IP, PORTA_DEST, p1);
					for(int i = 0; i <= 3; i++)
						System.out.println("Tempo: " + time);
					
				
				} else 
					if(time <= 7) {
						time = 0;
						time += TIMEOUT;
						System.out.println("Tentando retransmitir");
						rede.enviar(socket, ENDERECO_IP, PORTA_DEST, p1);
						for(int i = 0; i <= 7; i++)
							System.out.println("Tempo: " + time);
						
					
				}else if(time <= 15) {
					time = 0;
					time += TIMEOUT;
					System.out.println("Tentando retransmitir");
					rede.enviar(socket, ENDERECO_IP, PORTA_DEST, p1);
					for(int i = 0; i <= 7; i++)
						System.out.println("Tempo: " + time);
					
					
				}else {
					break;
				}
				
				

				// Iterar através do hashmap para verificar se os pacotes serão reenviados
				Iterator it = pacoteEnviado.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Pacote, Integer> pair = (Map.Entry)it.next();

					Pacote key = pair.getKey();
					int timeOut = pair.getValue();

					// Se o tempo atingir o tempo limite no hashmap, envie o pacote novamente
					if(timeOut == time){
						rede.enviar(socket, ENDERECO_IP, PORTA_DEST, key);

						// Tempo limite mais rápido em LOSS_RATE mais alto
						if(LOSS_RATE < 0.8) {
							pacoteEnviado.put(key, timeOut * 2);
						}
							
						else {
							pacoteEnviado.put(key, timeOut + 200);
						}
							

						System.out.println("Time:" + time);
						System.out.println("TimeOut:" + timeOut + "\n");
					}
				}

			}

		}

		//socket.close();
	}
        // Atualizando o número de sequencia com a adição do tamanho
        public static int updateSeqNum(int length){
            envioSeqNum += length;
            return envioSeqNum;
        }

}
