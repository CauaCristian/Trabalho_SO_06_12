import java.util.*;

public class MemoryManagement {

    static final int MEMORY_SIZE = 32;
    static int[] memory = new int[MEMORY_SIZE];
    static Map<String, Integer> processMap = new HashMap<>();

    public static void main(String[] args) {
        // Simulação dos processos
        List<Process> processes = List.of(
                new Process("P1", 5), new Process("P2", 4), new Process("P3", 2),
                new Process("P4", 5), new Process("P5", 8), new Process("P6", 3),
                new Process("P7", 5), new Process("P8", 8), new Process("P9", 2),
                new Process("P10", 6));

        System.out.println("--- Simulação First Fit ---");
        simulate(processes, MemoryManagement::firstFit);

        System.out.println("--- Simulação Next Fit ---");
        simulate(processes, MemoryManagement::nextFit);

        System.out.println("--- Simulação Best Fit ---");
        simulate(processes, MemoryManagement::bestFit);

        System.out.println("--- Simulação Worst Fit ---");
        simulate(processes, MemoryManagement::worstFit);
    }

    private static void simulate(List<Process> processes, AllocationAlgorithm algorithm) {
        Arrays.fill(memory, 0);
        processMap.clear();
        Random random = new Random();

        for (int i = 0; i < 30; i++) {
            Process process = processes.get(random.nextInt(processes.size()));
            if (processMap.containsKey(process.id)) {
                deallocate(process);
            } else {
                allocate(process, algorithm);
            }
            printMemoryState();
        }
    }

    private static boolean allocate(Process process, AllocationAlgorithm algorithm) {
        int index = algorithm.allocate(memory, process.size);
        if (index != -1) {
            for (int i = 0; i < process.size; i++) {
                memory[index + i] = 1;
            }
            processMap.put(process.id, index);
            System.out.println("Processo " + process.id + " alocado no bloco " + index);
            return true;
        } else {
            System.out.println("Erro ao alocar processo " + process.id);
            return false;
        }
    }

    private static void deallocate(Process process) {
        int index = processMap.get(process.id);
        for (int i = 0; i < process.size; i++) {
            memory[index + i] = 0;
        }
        processMap.remove(process.id);
        System.out.println("Processo " + process.id + " desalocado do bloco " + index);
    }

    private static void printMemoryState() {
        System.out.println(Arrays.toString(memory));
    }

    // Algoritmos de gerenciamento

    private static int firstFit(int[] memory, int size) {
        for (int i = 0; i <= MEMORY_SIZE - size; i++) {
            if (isFree(memory, i, size)) {
                return i;
            }
        }
        return -1;
    }

    private static int nextFit(int[] memory, int size) {
        int lastPosition = processMap.isEmpty() ? 0 : Collections.max(processMap.values());
        for (int i = lastPosition; i < MEMORY_SIZE; i++) {
            if (isFree(memory, i, size)) {
                return i;
            }
        }
        for (int i = 0; i < lastPosition; i++) {
            if (isFree(memory, i, size)) {
                return i;
            }
        }
        return -1;
    }

    private static int bestFit(int[] memory, int size) {
        int bestIndex = -1;
        int smallestFit = Integer.MAX_VALUE;

        for (int i = 0; i <= MEMORY_SIZE - size; i++) {
            if (isFree(memory, i, size)) {
                int fit = calculateFreeSpace(memory, i);
                if (fit < smallestFit) {
                    bestIndex = i;
                    smallestFit = fit;
                }
            }
        }
        return bestIndex;
    }

    private static int worstFit(int[] memory, int size) {
        int worstIndex = -1;
        int largestFit = -1;

        for (int i = 0; i <= MEMORY_SIZE - size; i++) {
            if (isFree(memory, i, size)) {
                int fit = calculateFreeSpace(memory, i);
                if (fit > largestFit) {
                    worstIndex = i;
                    largestFit = fit;
                }
            }
        }
        return worstIndex;
    }

    private static boolean isFree(int[] memory, int start, int size) {
        for (int i = start; i < start + size; i++) {
            if (memory[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private static int calculateFreeSpace(int[] memory, int start) {
        int freeSpace = 0;
        for (int i = start; i < MEMORY_SIZE && memory[i] == 0; i++) {
            freeSpace++;
        }
        return freeSpace;
    }

    // Classes Auxiliares

    static class Process {
        String id;
        int size;

        Process(String id, int size) {
            this.id = id;
            this.size = size;
        }
    }

    @FunctionalInterface
    interface AllocationAlgorithm {
        int allocate(int[] memory, int size);
    }
}