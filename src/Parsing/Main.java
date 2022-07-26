package Parsing;

public class Main {
   
    
    public static void main(String[] args) {
       pruebaClassToken();
    }
    
    static void pruebaClassToken(){
        System.out.println("Nombres de c/u de los tokens:" );
        Token t = new Token();
        
        for (int i = Token.FIN; i<=Token.TIPO; i++){
            t.set(i, 0);
            System.out.println(t);
        }       
        System.out.println();
        
        System.out.println("Nombre de Tokens a partir de un char auxiliar:" ); 
        final char aux[]={'/','%','+','-','*', ',', ':', '.', ';', '(', ')', '#'};
        
        for (int i = 0; i<aux.length; i++){
            int nombre = Token.getNomToken(aux[i]);
            System.out.println("char '"+aux[i]+"', Nombre de token="+Token.getNomAsString(nombre));
        }
        
        System.out.println();
    }
    
}
