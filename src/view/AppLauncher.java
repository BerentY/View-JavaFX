package view;

/**
 * Görevi: JavaFX'in katı başlatma kurallarını aşmak için aracı olmak.
 */
public class AppLauncher {
    public static void main(String[] args) {
        // JavaFX'i kandırıyoruz: Doğrudan Application'ı değil,
        // normal bir sınıf üzerinden Main sınıfını çağırıyoruz.
        Main.main(args);
    }
}