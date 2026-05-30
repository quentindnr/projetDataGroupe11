public class Vecteur {
    private double[] composantes;
    private int dimension;
    private int norme;

    public Vecteur(double[] composantes, int dimension) {
        this.composantes = composantes;
        this.dimension = dimension;
    }

    public double getComposantesAvecIndex(int i) {
        return composantes[i];
    }

    public void setComposantesAvecIndex(int i, int value) {
        composantes[i] = value;
    }

    public double[] getComposantes(){
        return this.composantes;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void ajouter(Vecteur v) {
        if (this.dimension != v.dimension) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir la même dimension pour être ajoutés.");
        }
        for (int i = 0; i < this.dimension; i++) {
            this.composantes[i] += v.composantes[i];
        }
    }

    public void soustraire(Vecteur v) {
        if (this.dimension != v.dimension) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir la même dimension pour être soustraits.");
        }
        for (int i = 0; i < this.dimension; i++) {
            this.composantes[i] += v.composantes[i];
        }
    }

    public void multiplier(Vecteur v) {

        if (this.dimension != v.dimension) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir la même dimension pour être multipliés.");
        }
        for (int i = 0; i < this.dimension; i++) {
            this.composantes[i] *= v.composantes[i];
        }
    }

    public double[][] produitScalaire(Vecteur v) {
        if (this.dimension != v.dimension) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir le même nombre de dimension pour être ajoutés.");
        }
        double[][] result = new double[this.dimension][this.dimension];
        for (int i = 0; i < this.dimension; i++) {
            for (int j = 0; j < this.dimension; j++) {
                result[i][j] = this.composantes[i] * v.composantes[j];
            }
        }
        return result;
    }

    public int getNorme() {
        int sum = 0;
        for (int i = 0; i < this.dimension; i++) {
            sum += this.composantes[i] * this.composantes[i];
        }
        this.norme = (int) Math.sqrt(sum);
        return norme;
    }

    public double distance(Vecteur v) {
        if (this.dimension != v.dimension) {
            throw new IllegalArgumentException("Les vecteurs doivent avoir la même dimension pour calculer leur distance.");
        }
        int sum = 0;
        for (int i = 0; i < this.dimension; i++) {
            sum += Math.pow(this.composantes[i] - v.composantes[i], 2);
        }
        return Math.sqrt(sum);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.dimension - 1; i++) {
            stringBuilder.append(this.composantes[i]);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

}
