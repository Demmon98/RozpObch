package lab;

import mpi.MPI;

public class Sequential {
    public static void calculate(String[] args, int dimension) {
        MPI.Init(args);

        int processNumber = MPI.COMM_WORLD.Rank();

        Matrix A = new Matrix(dimension, "A");
        Matrix B = new Matrix(dimension, "B");
        Matrix C = new Matrix(dimension, "C");
        long time = 0L;

        if (processNumber == 0) {
            A.fillRandom(5);
            B.fillRandom(5);
            time = System.currentTimeMillis();
        }

        for (int i = 0; i < A.width; i++) {
            for (int j = 0; j < B.height; j++) {
                for (int k = 0; k < A.height; k++) {
                    C.matrix[i * A.width + j] += A.matrix[i * A.width + k] * B.matrix[k * B.width + j];
                }
            }
        }

        if (processNumber == 0) {
            System.out.print("Последовательный алгоритм [" + dimension + "x" + dimension + "]: ");
            System.out.println(System.currentTimeMillis() - time + " ms");
        }

        MPI.Finalize();
    }
}