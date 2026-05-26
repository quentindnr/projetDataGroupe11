public class Vecteur {
    private int composantes; 
    private int dimensions; 
    private int norme;

    public Vecteur(){
        
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
        if (this.dimensions != v.dimensions) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir le même nombre de dimensions pour être ajoutés.");
        }
        for (int i = 0; i < this.dimensions; i++) {
            this.composantes[i] += v.composantes[i];
        }
    }

    public void soustraire(Vecteur v){
        if (this.dimensions != v.dimensions) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir le même nombre de dimensions pour être ajoutés.");
        }
    }

    public void multiplier(Vecteur v){

    }

    public int produitScalaire(Vecteur v){

    }

    public int getNorme(){
        return norme;
    }

    public int distance(Vecteur v){
        
    }
}
