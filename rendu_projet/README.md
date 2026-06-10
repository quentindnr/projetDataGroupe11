# Reconnaissance faciale par Analyse en Composantes Principales (ACP)

Projet SAÉ — ING1 GM-GI, CY Tech (2025-2026) — **Groupe 11**

Application Java de reconnaissance faciale par la méthode des **eigenfaces**. Chaque visage est
représenté comme un vecteur de pixels, projeté dans un sous-espace de dimension réduite construit par
ACP. L'identification compare le visage testé aux visages de référence et décide, à l'aide de trois
seuils de rejet, s'il appartient ou non à la base.

## Auteurs

CASTERAA Othilie · CORREIA Eneko · DONIER Quentin · HILL Zak · LOGIE Thibaud

Encadrantes : Nisrine FORTIN CAMDAVANT · Elisabeth RANISAVLJEVIĆ

## Prérequis

- **Java JDK 21**
- **Apache Ant** (pour la compilation et le lancement)
- Aucune installation externe nécessaire : les bibliothèques sont fournies dans `lib/`
  - **EJML 0.44.0** (algèbre linéaire : valeurs/vecteurs propres)
  - **JavaFX 21** (interface graphique)

## Structure du projet

```
.
├── build.xml                 # Script de build Ant
├── lib/                      # Dépendances (EJML + JavaFX SDK)
├── archive/                  # Jeux de données (images PGM 92x112)
│   ├── train/                # Base d'apprentissage (construction de l'ACP)
│   ├── validation/           # Base de validation (calibration des seuils)
│   ├── test/                 # Base de test (évaluation finale)
│   └── base/                 # Base ORL complète (originale)
└── src/src/
    ├── Main.java             # Point d'entrée (lance l'IHM JavaFX)
    ├── systeme_reconnaissance/  # Cœur ACP : Image, Vecteur, Matrice, SousEspace,
    │                            # Eigenface, EVDCache, SystemeReconnaissance, LoiFisher…
    ├── ihm_graphique/        # Interface JavaFX (IhmGraphique, IhmVisuels)
    └── ihm_console/          # Benchmark console (IhmConsole)
```

## Compilation et exécution (Ant)

| Commande | Description |
|----------|-------------|
| `ant compile` | Compile les sources dans `build/` |
| `ant run` | Compile, package et **lance l'interface graphique** |
| `ant jar` | Génère `dist/facial-recognition.jar` |
| `ant javadoc` | Génère la documentation HTML dans `docs/` |
| `ant package` | Prépare l'archive de rendu autonome (`rendu_facial_recognition.zip`) |
| `ant clean` | Supprime les fichiers générés |
| `ant` (cible `all`) | Enchaîne clean → compile → jar → javadoc → package |

La cible `run` passe automatiquement la configuration JavaFX requise
(`--module-path … --add-modules javafx.controls,javafx.fxml,javafx.graphics`).

## Les trois points d'entrée

- **`Main`** (via `ant run`) — interface graphique JavaFX : on importe une image (glisser-déposer ou
  sélecteur de fichier) et l'application affiche l'identité reconnue ou « visage inconnu ». Un bouton
  **« PCA visuals »** ouvre la fenêtre des visuels (`IhmVisuels`) : visage moyen, premiers eigenfaces,
  visages centrés et reconstructions pour plusieurs valeurs de K.
- **`ihm_console.IhmConsole`** — benchmark en ligne de commande suivant la méthodologie à trois bases
  (apprentissage → validation → test) : il calibre les seuils sur la validation puis mesure le taux
  d'identification sur la base de test.

## Méthode

1. **Prétraitement** — images en niveaux de gris, taille uniforme 92×112, format PGM.
2. **Vectorisation** — chaque image devient un vecteur (parcours en « serpent »), et inversement.
3. **ACP** — visage moyen, centrage, valeurs/vecteurs propres de la covariance réduite
   `(1/m)·AᵀA` (astuce de Turk-Pentland), sélection des K composantes par variance cumulée.
4. **Identification** — projection du visage testé puis recherche du plus proche voisin.
5. **Robustesse (rejet des inconnus)** — décision combinée à partir de trois seuils calibrés sur la
   base de validation :
   - **Θd** : distance minimale au plus proche voisin ;
   - **Θr** : erreur de reconstruction (percentile 95-99 %) ;
   - **T²α** : statistique de Hotelling T² comparée au seuil théorique issu de la **loi de Fisher**.

   Un visage est reconnu si `T² < T²α` **et** `erreur < Θr` **et** `distance < Θd`, sinon il est
   rejeté comme inconnu.

## Jeu de données

Base **ORL / AT&T** (« The ORL face database », Olivetti Research Laboratory, Cambridge) : 40 sujets,
10 images chacun, 92×112 pixels, niveaux de gris. Les images sont réparties en trois bases
(apprentissage / validation / test) ; certaines identités sont volontairement exclues de
l'apprentissage pour servir d'intrus et tester le rejet.


