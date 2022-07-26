package Parsing;

public class ErrorMgr { //Manejador de Errores.
    private String errorMsj;
    private int pos;
    private String lexema;
    
    public ErrorMgr(){
        init();
        lexema = "";
    }
    
    public void init(){
        errorMsj = null;
    }
    
    public void setError(String errorMsj, int posLexema, String lexema){
        if (hayError())
            return;         //Ya hay un error previo.
        
        if (errorMsj == null)
            errorMsj = "";
        
        this.errorMsj = errorMsj;
        this.pos      = posLexema;
        this.lexema   = lexema;
    }
    
    public boolean hayError(){
        return (errorMsj != null);
    }
    
    public String getErrorMsj(){
        return (hayError() ? errorMsj : "");
    }
    
    public String getLexema(){
        return lexema;
    }
    
    public int getPosLexema(){
        return pos;
    }
}

