package lab;

import mpi.MPI;

public class RibbonScheme {
    public static void calculate(String[] args, int dimension) {
        MPI.Init(args);

        int processNumber = MPI.COMM_WORLD.Rank();

        int threadsNumber = MPI.COMM_WORLD.Size();

        Matrix A = new Matrix(dimension, "A");
        Matrix B = new Matrix(dimension, "B");
        Matrix C = new Matrix(dimension, "C");
        long time = 0L;

        if (processNumber == 0) {
            B.fillRandom(5);
            A.fillRandom(5);
            time = System.currentTimeMillis();
        }

        int lineHeight = dimension /  threadsNumber;

        int[] bufferA = new int[lineHeight * dimension];
        int[] bufferB = new int[lineHeight * dimension];
        int[] bufferC = new int[lineHeight * dimension];

        MPI.COMM_WORLD.Scatter(A.matrix, 0, lineHeight * dimension, MPI.INT, bufferA, 0, lineHeight * dimension, MPI.INT, 0);
        MPI.COMM_WORLD.Scatter(B.matrix, 0, lineHeight * dimension, MPI.INT, bufferB, 0, lineHeight * dimension, MPI.INT, 0);
        int nextProcess = (processNumber + 1) %  threadsNumber;
        int previousProcess = processNumber - 1;
        if (previousProcess < 0)
            previousProcess =  threadsNumber - 1;
        int prevDataNum = processNumber;

        for (int p = 0; p <  threadsNumber; p++) {
            for (int i = 0; i < lineHeight; i++)
                for (int j = 0; j < dimension; j++)
                    for (int k = 0; k < lineHeight; k++)
                        bufferC[i * dimension + j] += bufferA[prevDataNum * lineHeight + i * dimension + k] * bufferB[k * dimension + j];
            prevDataNum -= 1;
            if (prevDataNum < 0)
                prevDataNum =  threadsNumber - 1;

            MPI.COMM_WORLD.Sendrecv_replace(bufferB, 0, lineHeight * dimension, MPI.INT, nextProcess, 0, previousProcess, 0);
        }

        MPI.COMM_WORLD.Gather(bufferC, 0, lineHeight * dimension, MPI.INT, C.matrix, 0, lineHeight * dimension, MPI.INT, 0);

        if (processNumber == 0) {
            System.out.print("Ленточная схема [" + dimension + "x" + dimension + "]: ");
            System.out.println(System.currentTimeMillis() - time + " ms");
        }
        MPI.Finalize();
    }
}