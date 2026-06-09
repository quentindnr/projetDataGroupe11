package systeme_reconnaissance;

/**
 * Une classe qui calcule le quantile de la loi de Fisher F(d1, d2)
 *
 * Aucune bibliotheque du projet ne propose de loi statistique. Le quantile est donc obtenu en
 * inversant la fonction de repartition de la loi de Fisher, qui s'exprime a l'aide de la fonction
 * beta incomplete. L'inversion est faite par dichotomie.
 */
public final class LoiFisher {

    /**
     * Classe utilitaire, pas d'instanciation
     */
    private LoiFisher() {
    }

    /**
     * Calcule le quantile d'ordre proba de la loi de Fisher F(d1, d2). La fonction de repartition de
     * la loi vaut I_t(d1/2, d2/2) avec t = d1*x / (d1*x + d2), on inverse donc la fonction beta
     * incomplete puis on revient a x
     *
     * @param proba L'ordre du quantile entre 0 et 1
     * @param d1    Le premier nombre de degres de liberte
     * @param d2    Le second nombre de degres de liberte
     * @return La valeur x telle que P(X <= x) = proba
     */
    public static double quantile(double proba, double d1, double d2) {
        if (proba <= 0) {
            return 0;
        }
        if (proba >= 1) {
            return Double.POSITIVE_INFINITY;
        }

        double y = inverseBetaIncomplete(proba, d1 / 2.0, d2 / 2.0);
        return (d2 * y) / (d1 * (1.0 - y));
    }

    /**
     * Inverse la fonction beta incomplete par dichotomie, renvoie x tel que I_x(a, b) = proba
     *
     * @param proba La valeur cible entre 0 et 1
     * @param a     Le premier parametre de forme
     * @param b     Le second parametre de forme
     * @return La valeur x entre 0 et 1 correspondante
     */
    private static double inverseBetaIncomplete(double proba, double a, double b) {
        double bas = 0.0;
        double haut = 1.0;
        for (int i = 0; i < 100; i++) {
            double milieu = 0.5 * (bas + haut);
            if (betaIncomplete(a, b, milieu) < proba) {
                bas = milieu;
            } else {
                haut = milieu;
            }
        }
        return 0.5 * (bas + haut);
    }

    /**
     * Calcule la fonction beta incomplete I_x(a, b)
     *
     * @param a Le premier parametre de forme
     * @param b Le second parametre de forme
     * @param x Le point d'evaluation entre 0 et 1
     * @return La valeur de I_x(a, b)
     */
    private static double betaIncomplete(double a, double b, double x) {
        if (x <= 0) {
            return 0;
        }
        if (x >= 1) {
            return 1;
        }

        double facteur = Math.exp(
                logGamma(a + b) - logGamma(a) - logGamma(b) + a * Math.log(x) + b * Math.log(1 - x));

        if (x < (a + 1) / (a + b + 2)) {
            return facteur * fractionContinueBeta(a, b, x) / a;
        }
        return 1 - facteur * fractionContinueBeta(b, a, 1 - x) / b;
    }

    /**
     * Calcule le developpement en fraction continue utilise par la fonction beta incomplete
     *
     * @param a Le premier parametre de forme
     * @param b Le second parametre de forme
     * @param x Le point d'evaluation
     * @return La valeur de la fraction continue
     */
    private static double fractionContinueBeta(double a, double b, double x) {
        final double minimum = 1.0e-30;
        double qab = a + b;
        double qap = a + 1;
        double qam = a - 1;
        double c = 1;
        double d = 1 - qab * x / qap;
        if (Math.abs(d) < minimum) {
            d = minimum;
        }
        d = 1 / d;
        double resultat = d;

        for (int m = 1; m <= 200; m++) {
            int m2 = 2 * m;
            double aa = m * (b - m) * x / ((qam + m2) * (a + m2));
            d = 1 + aa * d;
            if (Math.abs(d) < minimum) {
                d = minimum;
            }
            c = 1 + aa / c;
            if (Math.abs(c) < minimum) {
                c = minimum;
            }
            d = 1 / d;
            resultat *= d * c;

            aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
            d = 1 + aa * d;
            if (Math.abs(d) < minimum) {
                d = minimum;
            }
            c = 1 + aa / c;
            if (Math.abs(c) < minimum) {
                c = minimum;
            }
            d = 1 / d;
            double delta = d * c;
            resultat *= delta;

            if (Math.abs(delta - 1) < 1.0e-10) {
                break;
            }
        }
        return resultat;
    }

    /**
     * Calcule le logarithme de la fonction gamma avec l'approximation de Lanczos
     *
     * @param x L'argument strictement positif
     * @return La valeur de ln(gamma(x))
     */
    private static double logGamma(double x) {
        double[] coefficients = {
                76.18009172947146, -86.50532032941677, 24.01409824083091,
                -1.231739572450155, 0.1208650973866179e-2, -0.5395239384953e-5
        };
        double y = x;
        double tmp = x + 5.5;
        tmp -= (x + 0.5) * Math.log(tmp);
        double serie = 1.000000000190015;
        for (int j = 0; j < coefficients.length; j++) {
            y += 1;
            serie += coefficients[j] / y;
        }
        return -tmp + Math.log(2.5066282746310005 * serie / x);
    }
}
