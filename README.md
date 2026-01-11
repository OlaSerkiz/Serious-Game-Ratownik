# 🌊 Ratownik - Symulacja Szkoleniowa

**Ratownik** to symulacja edukacyjna stworzona w języku Java przy użyciu biblioteki **JavaFX**. Celem gry jest ćwiczenie umiejętności szybkiego reagowania, myślenia i podejmowania krytycznych decyzji, które mogą wpłynąć na ludzkie życie.

## 🚀 Funkcje Projektu

* **Mapa Poziomów**: Poruszanie się postacią ratownika w czasie rzeczywistym (WASD) w celu wyboru poziomu trudności gry.
* **Różnorodne Scenariusze**:
    * **Pytania zamknięte (ABC)**: Szybkie decyzje pod presją czasu (im szybciej tym więcej punktów).
    * **Pytania otwarte**: Wymagają wpisania poprawnej odpowiedzi.
    * **Zadania Interaktywne (Drag & Drop)**: Przeciąganie odpowiedniego sprzętu ratunkowego (np. koła, apteczki) na cel.
* **System Pauzy/Wznowienia**: Możliwość zatrzymania gry w dowolnym momencie – animacje i licznik czasu zostają zamrożone, a ekran ulega estetycznemu zaciemnieniu. Po wznowieniu gra wraca do pierwotnego stanu.
* **Raportowanie Wyników**: Automatyczny zapis każdego ukończonego dyżuru do pliku zewnętrznego `ranking.txt`. W pliku znajdują się dane o dacie i czasie, nazwie gracza, liczba punktów oraz numer poziomu.
* **Dynamiczne Animacje**: Obiekty w grze (ratownik, tonący, dziecko na plaży) posiadają płynne animacje ruchu i skalowania.

## 🎮 Instrukcja Sterowania

| Klawisz / Akcja | Funkcja                                                                       |
| :--- |:------------------------------------------------------------------------------|
| **W, A, S, D** | Poruszanie się ratownikiem po mapie poziomów                                  |
| **ENTER** | Wybór podświetlonego poziomu na mapie oraz zatwierdzenie wpisanych odpowiedzi |
| **Myszka** | Wybór odpowiedzi ABC oraz przeciąganie przedmiotów (Drag & Drop)              |

## 🛠️ Technologie i Architektura

* **Język**: Java
* **Grafika**: JavaFX (Transitions, AnimationTimer, CSS Styling)
* **Główna realizacja kodu**:
    * `RaportZGry`: Osobna klasa odpowiedzialna za operacje wejścia/wyjścia (I/O).
    * `OknoGry`: Zarządzanie interfejsem użytkownika i efektami wizualnymi.
    * `MozliweScetiusze`: Baza scenariuszy realizowanych w trakcie gry

## 📂 Struktura Pliku Raportu

Każdy zakończony "Koniec dnia" generuje wpis w pliku `ranking.txt` w formacie:
`[RRRR-MM-DD HH:MM:SS] Gracz: [IMIĘ] | Punkty: [PUNKTY] | Poziom: [NR]`

---
*Projekt stworzony jako pomoc dydaktyczna w szkoleniach ratowniczych.*