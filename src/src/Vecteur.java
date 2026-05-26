public class Vecteur {
    private int[] composantes;
    private int dimensions;
    private int norme;

    public Vecteur(int[] composantes, int dimension){
        this.composantes = composantes;
        this.dimensions = dimension;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i<this.dimensions-1; i++){
            stringBuilder.append(this.composantes[i]);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
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

    public void produitScalaire(Vecteur v){

    }

    public int getNorme(){
        return norme;
    }

    public void distance(Vecteur v){
        
    }
}
