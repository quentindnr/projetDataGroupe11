public class Vecteur {
    private int[] composantes;
    private int dimensions;
    private int norme;

    public Vecteur(int[] composantes, int dimension){
        this.composantes = composantes;
        this.dimensions = dimension;
    }   
    public int getComposantesAvecIndex (int i ){
        return composantes[i];
    }

    private void setComposantesAvecIndex (int i, int value){
        composantes[i] = value;
    }

    public int getDimensions() {
        return dimensions;
    }

    private void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public void ajouter(Vecteur v){
        
    }

    public void soustraire(Vecteur v){
       
    }

    public void multiplier(Vecteur v){

    }

    public void produitScalaire(Vecteur v){

    }

    public int getNorme(){
        return norme;
    }

    public void distance(Vecteur v){
        
    }
}
