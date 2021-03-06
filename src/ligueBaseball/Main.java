package ligueBaseball;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.StringTokenizer;

public class Main {
	private static GestionLigueBaseball gestionLigue;
	private static boolean lectureAuClavier;
	public static BufferedReader cin = new BufferedReader(
			new InputStreamReader(System.in));
	public static void main(String[] args) {
		// validation du nombre de param�tres
				if (args.length < 3) {
					System.out.println("Usage: java ligueBaseball.Main <userId> <motDePasse> <baseDeDonnees> [<fichier-entree>]");
					System.out.println(Connexion.serveursSupportes());
					return;
				}

				try {
					lectureAuClavier = true;
					InputStream sourceTransaction = System.in;
					if (args.length > 3) {
						sourceTransaction = new FileInputStream(args[3]);
						lectureAuClavier = false;
					}
					BufferedReader reader = new BufferedReader(new InputStreamReader(
							sourceTransaction));

					gestionLigue = new GestionLigueBaseball("postgres", args[0], args[1], args[2]);
					traiterTransactions(reader);
				} catch (Exception e) {
					e.printStackTrace(System.out);
				} finally {
					try {
						gestionLigue.fermer();
					} catch (SQLException e) {
						e.printStackTrace(System.out);
					}
				}
	}
	/**
	 * Traitement des transactions de la bibliotheque
	 */
	static void traiterTransactions(BufferedReader reader) throws Exception {
		afficherAide();
		String transaction = lireTransaction(reader);
		while (!finTransaction(transaction)) {
			/* decoupage de la transaction en mots */
			StringTokenizer tokenizer = new StringTokenizer(transaction, " ");
			if (tokenizer.hasMoreTokens())
				executerTransaction(tokenizer);
			transaction = lireTransaction(reader);
		}
	}

	/**
	 * Lecture d'une transaction
	 */
	static String lireTransaction(BufferedReader reader) throws IOException {
		System.out.print("> ");
		String transaction = reader.readLine();
		/* echo si lecture dans un fichier */
		if (!lectureAuClavier)
			System.out.println(transaction);
		return transaction;
	}
	
	static void executerTransaction(StringTokenizer tokenizer) throws SQLException, LigueBaseballException, Exception{
		String commande = tokenizer.nextToken();
		
		if("aide".startsWith(commande)){
			afficherAide();
		}
		else if("afficherEquipes".startsWith(commande)){
			gestionLigue.gestionEquipe.getEquipes();
		}
		else if("supprimerEquipe".startsWith(commande)){
			gestionLigue.gestionEquipe.supprimer(readString(tokenizer));
		}
		else if("creerEquipe".startsWith(commande)){
			if(tokenizer.countTokens() == 2){
				gestionLigue.gestionEquipe.ajout(readString(tokenizer), readString(tokenizer));
			} else if(tokenizer.countTokens() == 3){
				gestionLigue.gestionEquipe.ajout(readString(tokenizer), readString(tokenizer), readString(tokenizer));
			}
		}
		else if("--".startsWith(commande)){
			//Ligne de commentaire, ne rien faire et passer a la prochaine ligne
		}
	}

	/**
	 * La methode afficherAide sert a afficher l'aide pour les personnes qui ne seraient pas les options.
	 */
	static void afficherAide() {
		System.out.println();
		System.out.println("Chaque transaction comporte un nom et une liste d'arguments");
		System.out.println("separes par des espaces. La liste peut etre vide.");
		System.out.println(" Les dates sont en format yyyy-mm-dd.");
		System.out.println("");
		System.out.println("Les transactions sont:");
		System.out.println("  aide");
		System.out.println("  exit");
		System.out.println("  creerEquipe <EquipeNom> [<NomTterrain> AdresseTerrain]");
		System.out.println("  afficherEquipes");
		System.out.println("  supprimerEquipe <EquipeNom>");
	}
	

	static boolean finTransaction(String transaction) {
		if (transaction == null)
			return true;

		StringTokenizer tokenizer = new StringTokenizer(transaction, " ");

		/* ligne ne contenant que des espaces */
		if (!tokenizer.hasMoreTokens())
			return false;

		/* commande "exit" */
		String commande = tokenizer.nextToken();
		if (commande.equals("exit"))
			return true;
		else
			return false;
	}
	/** lecture d'une chaine de caracteres de la transaction entree � l'ecran */
	static String readString(StringTokenizer tokenizer) throws LigueBaseballException {
		if (tokenizer.hasMoreElements())
			return tokenizer.nextToken();
		else
			throw new LigueBaseballException("autre parametre attendu");
	}

	/**
	 * lecture d'un int java de la transaction entree � l'ecran
	 */
	static int readInt(StringTokenizer tokenizer) throws LigueBaseballException {
		if (tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
			try {
				return Integer.valueOf(token).intValue();
			} catch (NumberFormatException e) {
				throw new LigueBaseballException("Nombre attendu a la place de \""
						+ token + "\"");
			}
		} else
			throw new LigueBaseballException("autre parametre attendu");
	}

	/**
	 * lecture d'un long java de la transaction entree a l'ecran
	 */
	static long readLong(StringTokenizer tokenizer) throws LigueBaseballException {
		if (tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
			try {
				return Long.valueOf(token).longValue();
			} catch (NumberFormatException e) {
				throw new LigueBaseballException("Nombre attendu a la place de \""
						+ token + "\"");
			}
		} else
			throw new LigueBaseballException("autre parametre attendu");
	}

	/**
	 * lecture d'une date en format YYYY-MM-DD
	 */
	static java.util.Date readDate(StringTokenizer tokenizer)
			throws LigueBaseballException {
		if (tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
			try {
				return FormatDate.convertirDate(token);
			} catch (ParseException e) {
				throw new LigueBaseballException(
						"Date en format YYYY-MM-DD attendue a la place  de \""
								+ token + "\"");
			}
		} else
			throw new LigueBaseballException("autre parametre attendu");
	}

}
