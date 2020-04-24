package main_package;

class Fighter {
    int id;
    int energyTsy;
    private int count_of_victories = 0;

    Fighter(int id, int energyTsy) {
        this.id = id; this.energyTsy = energyTsy;
    }
    void goForward() {
        count_of_victories++;
    }
}
