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

    public void setComposantesAvecIndex (int i, int value){
        composantes[i] = value;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
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
        for (int i = 0; i < this.dimensions; i++) {
            this.composantes[i] += v.composantes[i];
        }
    }

    public void multiplier(Vecteur v){
        
        if (this.dimensions != v.dimensions) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir le même nombre de dimensions pour être ajoutés.");
        }
        for (int i = 0; i < this.dimensions; i++) {
            this.composantes[i] *= v.composantes[i];
        }
    }

    public int[][] produitScalaire(Vecteur v){
        if (this.dimensions != v.dimensions) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir le même nombre de dimensions pour être ajoutés.");
        }
        int[][] result = new int[this.dimensions][this.dimensions];
        for (int i = 0; i < this.dimensions; i++) {
            for (int j = 0; j < this.dimensions; j++) {
                result[i][j] = this.composantes[i] * v.composantes[j];
            }
        }
        return result;
    }

    public int getNorme(){
        int sum = 0;
        for(int i = 0; i < this.dimensions; i++){
            sum += this.composantes[i] * this.composantes[i];
        }
        this.norme = (int) Math.sqrt(sum);
        return norme;
    }

    public void distance(Vecteur v){
        if (this.dimensions != v.dimensions) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir le même nombre de dimensions pour être ajoutés.");
        }
        int sum = 0;
        for (int i = 0; i < this.dimensions; i++) {
            sum += Math.pow(this.composantes[i] - v.composantes[i], 2);
        }
        double distance = Math.sqrt(sum);
        System.out.println("La distance entre les deux vecteurs est : " + distance); 
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

}
