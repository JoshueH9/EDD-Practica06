package fciencias.edatos.practica06;

import java.lang.Math;
import java.util.Scanner;
import java.util.InputMismatchException;

/**
* Implementación de árbol AVL
* @author Emmanuel Cruz Hernández.
* @version 3.0 Noviembre 2021 (Anterior 2.0 Julio 2021).
* @since Estructuras de Datos 2022-1.
*/
public class AVLTree<K extends Comparable, T> implements TDABinarySearchTree<K, T>{

	/**
	 * Nodo de un arbol AVL.
	 */
	public class AVLNode{

		/** Altura del nodo. */
		public int altura;

		/** Hijo izquierdo. */
		public AVLNode izquierdo;

		/** Hijo derecho. */
		public AVLNode derecho;

		/** Padre del nodo. */
		public AVLNode padre;

		/** Elemento almacenado en el nodo. */
		public T elemento;

		/** Clave del nodo. */
		public K clave;

		/**
		 * Crea un nuevo nodo AVL
		 * @param element el elemento a almacenar.
		 * @param key la clave del nodo.
		 * @param padre el padre del nodo
		 */
		public AVLNode(T element, K key, AVLNode padre){
			elemento = element;
			clave = key;
			this.padre = padre;
			altura = this.getAltura();
		}

		/**
		 * Calcula la altura del nodo.
		 */
		public int getAltura(){
			// Si este nodo es hoja
			if(izquierdo == null && derecho==null){
				return 0;
			} else if(izquierdo != null && derecho != null){ // Dos hijos
				int max = izquierdo.getAltura() > derecho.getAltura() ? izquierdo.getAltura() : derecho.getAltura();
				return 1 + max;
			} else{ // Tiene solo un hijo
				boolean tieneIzquierdo = izquierdo!=null;
				return 1 + (tieneIzquierdo ? izquierdo.getAltura() : derecho.getAltura());
			}
		}

		/**
		 * Actualiza la altura del nodo.
		 */
		public void actualizaAltura(){
			this.altura = this.getAltura();
		}
	}

	private AVLNode raiz;

	@Override
	public T retrieve(K k){
		AVLNode node = retrieve(k,raiz);
		if(node == null)
			return null;
		return node.elemento;
	}

	/**
	 * Obtenia el nodo con una clave específica.
	 * @param k la clave a buscar
	 * @param actual el nodo actual
	 * @return el nodo con clave k o null si no existe.
	 */
	private AVLNode retrieve(K k, AVLNode actual){
		// Verificamos que actual es null
		if(actual == null)
			return null;

		int compare = k.compareTo(actual.clave);

		// Si existe el elemento
		if(compare == 0){
			return actual;
		}

		if(compare < 0){ // Caso del hijo izquiero
			return retrieve(k, actual.izquierdo);
		} else { // Caso del hijo derecho
			return retrieve(k, actual.derecho);
		}
	}

	@Override
	public void insert(T e, K k){
		if(raiz == null){ // Arbol vacío
			raiz = new AVLNode(e, k, null);
			return;
		}
		AVLNode v = insert(e, k, raiz);

		// Rebalancear a partir de v hasta raiz
		rebalancea(v);
	}

	/**
	 * Inserta un nodo de forma recursiva.
	 * @param e el elemento a insertar
	 * @param k es la clave del nodo a insertar
	 * @param actual el nodo actual
	 * @return 
	 */
	public AVLNode insert(T e, K k, AVLNode actual){
		if(k.compareTo(actual.clave)<0){ // Verificamos sobre el izquierdo
			if(actual.izquierdo == null){ // Insertamos en esa posición
				actual.izquierdo = new AVLNode(e, k, actual);
				return actual.izquierdo;
			} else { // Recursión sobre el izquierdo
				return insert(e, k, actual.izquierdo);
			}
		} else{ // Verificamos sobre la derecha
			if(actual.derecho == null){ // Insertamos en esa posición
				actual.derecho = new AVLNode(e, k, actual);
				return actual.derecho;
			} else { // Recursión sobre el derecho
				return insert(e, k, actual.derecho);
			}
		}
	}

	@Override
	public T delete(K k){
		AVLNode v = retrieve(k, raiz);

		// El elemento que queremos eliminar no está en el árbol
		if(v == null){
			return null;
		}

		T eliminado = v.elemento;

		// Eliminar con auxiliar
		AVLNode w = delete(v);

		// Rebalancear
		rebalancea(w);

		return eliminado;
	}

	private AVLNode delete(AVLNode v){
		if(v.izquierdo!=null && v.derecho!=null){ // Tiene dos hijos
			AVLNode mayor = findMax(v.izquierdo);
			swap(mayor, v);
			return delete(mayor);
		} else if(v.izquierdo==null && v.derecho==null){ // No tiene hijos
			boolean esIzquierdo = v.padre.izquierdo==v;
			if(esIzquierdo){
				v.padre.izquierdo = null;
			} else{
				v.padre.derecho = null;
			}
			return v.padre;
		} else{ // Sólo tiene un hijo
			boolean hijoIzquierdo = v.izquierdo!=null;
			if(hijoIzquierdo){
				swap(v, v.izquierdo);
				return delete(v.izquierdo);
			} else{
				swap(v, v.derecho);
				return delete(v.derecho);
			}
		}
	}

	@Override
	public T findMin(){
		return findMin(raiz).elemento;
	}

	/**
	 * Método auxiliar para findMin
	 * */
	private AVLNode findMin(AVLNode node){
		// Verificar que no sea vacío -> return null
		if(node==null)
			return null;

		AVLNode minimo = node;

		// Mientras sí tenga hijo izquierdo -> Que actual se mueva al izquierdo
		if(node.izquierdo!=null)
			minimo = findMin(node.izquierdo);
		else
			return minimo;	// Ya encontramos al nodo con clave menor

		return minimo;
	}

	@Override
	public T findMax(){
		return findMax(raiz).elemento;
	}

	/**
	 * Método auxiliar para findMax.
	 * */
	private AVLNode findMax(AVLNode node){
		// Verificar que no sea vacío -> return null
		if(node==null)
			return null;

		AVLNode maximo = node;

		// Mientras sí tenga hijo derecha -> Que actual se mueva al derecho
		if(node.derecho!=null)
			maximo = findMax(node.derecho);
		else
			return maximo;	// Ya encontramos al nodo con clave mayor

		return maximo;
	}

	/**
	 * Método auxiliar para delete, hace cambio entre 2 nodos.
	 * */
	private void swap(AVLNode v, AVLNode w){
		T value = v.elemento;
		K clave = v.clave;
		v.elemento = w.elemento;
		v.clave = w.clave;
		w.elemento = value;
		w.clave = clave;
	}

	@Override
	public void preorden(){
		this.preorden(raiz);
	}

	/**
	 * Método auxiliar de preorden.
	 * */
	private void preorden(AVLNode node){
		// Primero verifica la raiz
		if(node == null)
			return;

		System.out.println(node.elemento);

		// Aplica preorden al izquierdo
		preorden(node.izquierdo);

		// Aplica preorden al derecho
		preorden(node.derecho);
	}

	@Override
	public void inorden(){
		this.inorden(raiz);
	}

	/**
	 * Método auxiliar de inorden.
	 * */
	private void inorden(AVLNode node){
		// Primero verifica la raiz
		if(node == null)
			return;

		// Aplica inorden al izquierdo
		inorden(node.izquierdo);

		System.out.println(node.elemento);

		// Aplica inorden al derecho
		inorden(node.derecho);
	}

	@Override
	public void postorden(){
		this.postorden(raiz);
	}

	/**
	 * Método auxiliar de postorden.
	 * */
	private void postorden(AVLNode node){
		// Primero verifica la raiz
		if(node == null)
			return;

		// Aplica postorden al izquierdo
		postorden(node.izquierdo);

		// Aplica postorden al derecho
		postorden(node.derecho);

		System.out.println(node.elemento);
	}

	@Override
	public boolean isEmpty(){
		return raiz == null;
	}

	/**
	 * Metodo que revalancea un arbol AVL
	 * @param actual el nodo que a partir de el se rebalanceara el arbol.
	 * */
	public void rebalancea (AVLNode actual){

		
		if(actual == null)
			return;

		if(actual == raiz)
			return;


		AVLNode p = actual.padre;	//el padre de actual

		//Caso 1: Actual tiene hermano
		if(p.izquierdo != null && p.derecho != null){

			AVLNode h = p.izquierdo == actual ? p.derecho : p.izquierdo; 	//el hermano de actual

			int alturaActual = actual.getAltura();
			int alturaH = h.getAltura();

			int restriccion = Math.abs(alturaActual-alturaH);

			if(restriccion>=2){  //HAY DESBALANCEO

				if(p.izquierdo == actual){ //Ver si actual es hijo izquierdo.

					//Comparar alturas con sus hijos para ver si es desbalanceo en zigzag
					if(actual.izquierdo != null && actual.derecho != null){ //Tiene 2 hijos
						
						int alturaHD = actual.derecho.getAltura();
						if(alturaActual-1 == alturaHD){	// zigzag

							rotarIzquierda(actual);
							rotarDerecha(actual.padre.padre);
							rebalancea(actual.padre);
							return;

						} else{	//linea recta

							rotarDerecha(actual.padre);
							rebalancea(actual);
							return;
						}

					} else { //Tiene 1 hijo

						if(actual.derecho != null){		// zigzag

							rotarIzquierda(actual);
							rotarDerecha(actual.padre.padre);
							rebalancea(actual.padre);
							return;

						} else {	//linea recta

							rotarDerecha(actual.padre);
							rebalancea(actual);
							return;
						}

					}

				} else { //Actual es hijo DERECHO.

					//Comparar alturas con sus hijos para ver si es desbalanceo en zigzag
					if(actual.izquierdo != null && actual.derecho != null){ //Tiene 2 hijos
						
						int alturaHI = actual.izquierdo.getAltura();
						if(alturaActual-1 == alturaHI){ //zigzag

							rotarDerecha(actual);
							rotarIzquierda(actual.padre.padre);
							rebalancea(actual.padre);
							return;

						} else{ //linea recta

							rotarIzquierda(actual.padre);
							rebalancea(actual);
							return;
						}

					} else { //Tiene 1 hijo

						if(actual.izquierdo != null){		// zigzag

							rotarDerecha(actual);
							rotarIzquierda(actual.padre.padre);
							rebalancea(actual.padre);
							return;

						} else {	//linea recta

							rotarIzquierda(actual.padre);
							rebalancea(actual);
							return;
						}
					}

				}


			} else { 	//NO HAY DESBALANCEO
				rebalancea(actual.padre);
				return;
			}


		//Caso 2: Actual no tiene hermano		
		} else {

			int alturaActual = actual.getAltura();
			int restriccion = Math.abs(-1-alturaActual);

			if(restriccion>=2){  //HAY DESBALANCEO

				if(p.izquierdo == actual){ //Ver si actual es hijo izquierdo.

					//Comparar alturas con sus hijos para ver si es desbalanceo en zigzag
					if(actual.izquierdo != null && actual.derecho != null){ //Tiene 2 hijos
						
						int alturaHD = actual.derecho.getAltura();
						if(alturaActual-1 == alturaHD){	// zigzag

							rotarIzquierda(actual);
							rotarDerecha(actual.padre.padre);
							rebalancea(actual.padre);
							return;

						} else{	//linea recta

							rotarDerecha(actual.padre);
							rebalancea(actual);
							return;
						}

					} else { //Tiene 1 hijo

						if(actual.derecho != null){		// zigzag

							rotarIzquierda(actual);
							rotarDerecha(actual.padre.padre);
							rebalancea(actual.padre);
							return;

						} else {	//linea recta

							rotarDerecha(actual.padre);
							rebalancea(actual);
							return;
						}

					}

				} else { //Actual es hijo DERECHO.

					//Comparar alturas con sus hijos para ver si es desbalanceo en zigzag
					if(actual.izquierdo != null && actual.derecho != null){ //Tiene 2 hijos
						
						int alturaHI = actual.izquierdo.getAltura();
						if(alturaActual-1 == alturaHI){ //zigzag

							rotarDerecha(actual);
							rotarIzquierda(actual.padre.padre);
							rebalancea(actual.padre);
							return;

						} else{ //linea recta

							rotarIzquierda(actual.padre);
							rebalancea(actual);
							return;
						}

					} else { //Tiene 1 hijo

						if(actual.izquierdo != null){		// zigzag

							rotarDerecha(actual);
							rotarIzquierda(actual.padre.padre);
							rebalancea(actual.padre);
							return;

						} else {	//linea recta

							rotarIzquierda(actual.padre);
							rebalancea(actual);
							return;
						}
					}

				}

			} else { 	//NO HAY DESBALANCEO
				rebalancea(actual.padre);
				return;
			}
		}

	}


	public void rotarIzquierda(AVLNode actual){

		if(actual==null)
			return;
		
		if(actual==raiz){

			//nueva raiz
			raiz = actual.derecho;

			//auxiliar para guardar antigua raiz			
			AVLNode aux = raiz.padre;
			aux.derecho = null;
			raiz.padre = null;
			
			if(raiz.izquierdo == null){	//no tiene hijo izquierdo
				raiz.izquierdo = aux;
				aux.padre = raiz;
				return;
			} else {	//tiene hijo izquierdo
				AVLNode auxIzq = raiz.izquierdo; //Guarda el hijo izquierdo aparte

				//auxIzq.padre=null;
				//raiz.izquierdo = null;

				raiz.izquierdo = aux;	//escribe la antigua raiz como hijo izquierdo
				aux.padre = raiz;

				aux.derecho = auxIzq;
				auxIzq.padre = aux;	//Coloca al antiguo hijo izquierdo como hijo derecho de la antigua raiz.
				return;
			}			
		} else {

			AVLNode p = actual.padre;

			if(p.izquierdo == actual){  //Actual es hijo izquierdo

				if(actual.derecho.izquierdo == null){

					p.izquierdo = null;
					AVLNode aux = actual;
					aux.padre = null;

					AVLNode nuevo = aux.derecho;
					p.izquierdo = nuevo;
					nuevo.padre = p;

					aux.derecho = null;
					nuevo.izquierdo = aux;
					return;

				} else {

					p.izquierdo = null;
					AVLNode aux = actual;
					aux.padre = null;

					AVLNode nuevo = aux.derecho;
					p.izquierdo = nuevo;
					nuevo.padre = p;

					AVLNode nuevoI = nuevo.izquierdo;


					aux.derecho = nuevoI;
					nuevoI.padre = aux;
					nuevo.izquierdo = aux;	

					return;

				}

			} else {					//Actual es hijo derecho

				if(actual.derecho.izquierdo == null){

					p.derecho = null; //**
					AVLNode aux = actual;
					aux.padre = null;

					AVLNode nuevo = aux.derecho;
					p.derecho = nuevo; //**
					nuevo.padre = p;

					aux.derecho = null;
					nuevo.izquierdo = aux;
					return;

				} else {

					p.derecho = null; //**
					AVLNode aux = actual;
					aux.padre = null;

					AVLNode nuevo = aux.derecho;
					p.derecho = nuevo; //**
					nuevo.padre = p;

					AVLNode nuevoI = nuevo.izquierdo;


					aux.derecho = nuevoI;
					nuevoI.padre = aux;
					nuevo.izquierdo = aux;	

					return;

				}				

			}
		} 
	}

	public void rotarDerecha(AVLNode actual){
		if(actual==null)
			return;
		
		if(actual==raiz ){
			//nueva raiz
			raiz = actual.izquierdo;

			//auxiliar para guardar antigua raiz			
			AVLNode aux = raiz.padre;
			aux.izquierdo = null;
			raiz.padre = null;
			
			if(raiz.derecho == null){	//no tiene hijo derecho
				raiz.derecho = aux;
				aux.padre = raiz;
				return;
			} else {	//tiene hijo derecho
				AVLNode auxDer = raiz.derecho; //Guarda el hijo derecho aparte

				//auxDer.padre=null;
				//raiz.derecho = null;

				raiz.derecho = aux;	//escribe la antigua raiz como hijo derecho
				aux.padre = raiz;

				aux.izquierdo = auxDer;
				auxDer.padre = aux;	//Coloca al antiguo hijo derecho como hijo izquierdo de la antigua raiz.
				return;
			}			
		} else {

			AVLNode p = actual.padre;

			if(p.derecho == actual){  //Actual es hijo derecho

				if(actual.izquierdo.derecho==null){

					p.derecho = null; //**
					AVLNode aux = actual;
					aux.padre = null;

					AVLNode nuevo = aux.izquierdo;
					p.derecho = nuevo;	//**
					nuevo.padre = p;

					aux.izquierdo = null;
					nuevo.derecho = aux;
					return;
				} else {

					p.derecho = null; //**
					AVLNode aux = actual;
					aux.padre = null;

					AVLNode nuevo = aux.izquierdo;
					p.derecho = nuevo; //**
					nuevo.padre = p;

					AVLNode nuevoI = nuevo.derecho;

					aux.izquierdo = nuevoI;
					nuevoI.padre = aux;
					nuevo.derecho = aux;	
					return;

				}	

			} else {		//Actual es hijo izquierdo

				if(actual.izquierdo.derecho == null){

					p.izquierdo = null;
					AVLNode aux = actual;
					aux.padre = null;

					AVLNode nuevo = aux.izquierdo;

					p.izquierdo = nuevo;
					nuevo.padre = p;

					aux.izquierdo = null;
					nuevo.derecho = aux;
					return;

				} else {

					p.izquierdo = null;
					AVLNode aux = actual;
					aux.padre = null;

					AVLNode nuevo = aux.izquierdo;

					p.izquierdo = nuevo;
					nuevo.padre = p;

					AVLNode nuevoI = nuevo.derecho;


					aux.izquierdo = nuevoI;
					nuevoI.padre = aux;
					nuevo.derecho = aux;	
					return;
				}				

			}
		}
	}

	public static void main(String[] args) {
		AVLTree<Integer, String> tree = new AVLTree<>();
		int clave = 0;
		String cadena = "";

		String rojo = "\u001B[31m", verde = "\u001B[32m", amarillo = "\u001B[33m", morado = "\u001B[35m", blanco = "\u001B[37m";
        Scanner sc = new Scanner(System.in);
        System.out.println("PRACTICA 06 --- Arboles AVL\n\nPara mi practica implementé arboles que guarden listas de caracteres(String)\nY las claves con las que se ordenan son de tipo Integer.\n\n");
        System.out.println("──▒▒▒▒▒▒───▄████▄\n─▒─▄▒─▄▒──███▄█▀ \n─▒▒▒▒▒▒▒─▐████──█─█ \n─▒▒▒▒▒▒▒──█████▄ \n─▒─▒─▒─▒───▀████▀ \n");
        System.out.println("\nPresiona Enter para comenzar.");
        sc.nextLine();

		int opcion = 0;

        do{
	
	    System.out.println("\n"+verde+"HOLA BIENVENIDO AL MENU."+blanco+"\n");

	    System.out.println(verde+"Elije una de las siguientes opciones:"+blanco+"\n\n "
			       +amarillo+"1) "+verde+" Método Retrieve"+blanco+"\n "
			       +amarillo+"2) "+verde+" Método Insert"+blanco+"\n "
			       +amarillo+"3) "+verde+" Método Delete"+blanco+"\n "
			       +amarillo+"4) "+verde+" Método FindMin"+blanco+"\n "
			       +amarillo+"5) "+verde+" Método FindMax"+blanco+"\n "
			       +amarillo+"6) "+verde+" Recorrido con Preorden"+blanco+"\n "
			       +amarillo+"7) "+verde+" Recorrido con Inorden"+blanco+"\n "
			       +amarillo+"8) "+verde+" Recorrido con Postorden"+blanco+"\n "
			       +amarillo+"9) "+verde+" Método isEmpty"+blanco+"\n\n" //Aqui tiene 2 saltos de linea para que resalte mejor lo de salir del menú.
			       +amarillo+"10) "+verde+"Salir del menú"+blanco);
	    
	    try{  
			System.out.print(blanco+"\n"+amarillo+" Opcion: "+blanco+"\n");
			opcion = sc.nextInt();
		}catch(InputMismatchException ime){
			System.out.println(rojo+"ERROR: Ingresa un numero."+blanco);
			sc.nextLine();
		}catch(Exception e){
			System.out.println(rojo+"\n\nERROR INESPERADO EN EL CODIGO.\n\n");
		}

		    switch(opcion){
			
		    case 1:
		    	try{
		    		System.out.println(verde+"Inserta la clave a buscar:"+blanco);
		    		clave = sc.nextInt();

		    		if(tree.retrieve(clave)==null)
						System.out.println("El elemento con esa clave no existe en el arbol");
					else
						System.out.println("El elemento con esa clave es: "+tree.retrieve(clave));

					try{
            			Thread.sleep(3000);
        			}catch(InterruptedException ie){}  

		   		}catch(InputMismatchException ime){
					System.out.println(rojo+"ERROR: Ingresa un numero."+blanco);
					sc.nextLine();
				}catch(Exception e){
					System.out.println(rojo+"\n\nERROR INESPERADO EN EL CODIGO.\n\n");
				}
			break;

		    case 2:
		    	try{
		    		System.out.println(verde+"Inserta la clave en donde se guardara:"+blanco);
		    		clave = sc.nextInt();

		    		sc.nextLine();
		    		System.out.println(verde+"Inserta la cadena a guardar:"+blanco);
		    		cadena = sc.nextLine()+"";

		    		tree.insert(cadena, clave);

		    		System.out.println("La cadena "+cadena+" se a guardado con la clave "+clave);

					try{
            			Thread.sleep(3000);
        			}catch(InterruptedException ie){} 

		   		}catch(InputMismatchException ime){
					System.out.println(rojo+"ERROR: Ingresa un numero."+blanco);
					sc.nextLine();
				}catch(Exception e){
					System.out.println(rojo+"\n\nERROR INESPERADO EN EL CODIGO.\n\n");
				}
			break;

		    case 3:
		    	try{
		    		System.out.println(verde+"Inserta la clave del elemento a eliminar:"+blanco);
		    		clave = sc.nextInt();

		    		if(tree.retrieve(clave)==null)
						System.out.println("El elemento con esa clave no existe en el arbol");
					else{
						tree.delete(clave);
						System.out.println("El elemento con la clave "+clave+" se ha eliminado");
					}

					try{
            			Thread.sleep(3000);
        			}catch(InterruptedException ie){}  

		   		}catch(InputMismatchException ime){
					System.out.println(rojo+"ERROR: Ingresa un numero."+blanco);
					sc.nextLine();
				}catch(Exception e){
					System.out.println(rojo+"\n\nERROR INESPERADO EN EL CODIGO.\n\n");
				}
			break;

		    case 4:
		    	System.out.println("El elemento con clave con valor minimo del arbol es "+tree.findMin());
		    	try{
            		Thread.sleep(3000);
        		}catch(InterruptedException ie){} 
			break;

		    case 5:
		    	System.out.println("El elemento con clave con valor máximo del arbol es "+tree.findMax());
		    	try{
            		Thread.sleep(3000);
        		}catch(InterruptedException ie){} 
			break;

		    case 6:
		    	System.out.println("Recorrido del arbol en Preorden\n");
		    	tree.preorden();
		    	try{
            		Thread.sleep(3000);
        		}catch(InterruptedException ie){} 
			break;

		    case 7:
		    	System.out.println("Recorrido del arbol en Inorden\n");
		    	tree.inorden();
		    	try{
            		Thread.sleep(3000);
        		}catch(InterruptedException ie){} 
			break;

		    case 8:
		    	System.out.println("Recorrido del arbol en Postorden\n");
		    	tree.postorden();
		    	try{
            		Thread.sleep(3000);
        		}catch(InterruptedException ie){} 
			break;

		    case 9:		    	
		    	if(tree.isEmpty())
		    		System.out.println("El arbol es vacio");
		    	else
		    		System.out.println("El arbol NO es vacio");
		    	try{
            		Thread.sleep(3000);
        		}catch(InterruptedException ie){} 
			break;

		    case 10:
				System.out.println(rojo+"\n ¡Adioooos!\n"+blanco);
		    	try{
            		Thread.sleep(3000);
        		}catch(InterruptedException ie){} 
			break;
			
		    default:
				System.out.println(rojo+"\nEsa opción no es válida\n");
		    	try{
            		Thread.sleep(3000);
        		}catch(InterruptedException ie){} 			
			break;
		    }
		}while (opcion!=10);
	}
}