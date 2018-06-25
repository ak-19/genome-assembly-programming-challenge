package week03;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class Circulation {
    private static class Edge {
        int from, to, l, c, cR, flow, id;

        public Edge(int from, int to, int l, int c, int id) {
            this.from = from;
            this.to = to;
            this.l = l;
            this.c = c;
            this.cR = c - l;
            this.flow = 0;
            this.id = id;
        }
    }

    private static class FlowGraph {
        int[] flowMap;
        int flowResult = 0;
        List<Edge> edges;
        List[] graph;

        public FlowGraph(int numVertices) {
            this.graph = new ArrayList[numVertices + 2];
            for (int i = 0; i < numVertices + 2; ++i) {
                this.graph[i] = new ArrayList<Integer>();
            }
            this.edges = new ArrayList<Edge>();
            this.flowMap = new int[numVertices +  2];
        }

        public void addEdge(int from, int to, int l, int c) {
            Edge forward = new Edge(from, to, l, c, edges.size());
            this.graph[from].add(edges.size());
            this.edges.add(forward);

            Edge backward = new Edge(to, from, 0, 0, edges.size());
            this.graph[to].add(edges.size());
            this.edges.add(backward);

            flowMap[from] += l;
            flowMap[to] -= l;
        }

        public void addFlow(int flow, int edgeId) {
            this.edges.get(edgeId).flow += flow;
            this.edges.get(edgeId ^ 1).flow -= flow;

            this.edges.get(edgeId).cR -= flow;
            this.edges.get(edgeId ^ 1).cR += flow;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        //Scanner reader = new Scanner(System.in);
        Scanner reader = new Scanner(new FileInputStream("files/Circulation.txt"));

        int numVertices = reader.nextInt();
        int numEdges = reader.nextInt();
        FlowGraph graph = new FlowGraph(numVertices);
        for (int edgeId = 0; edgeId < numEdges; edgeId++) {
            int from = reader.nextInt() - 1;
            int to = reader.nextInt() - 1;
            int l = reader.nextInt();
            int c = reader.nextInt();
            graph.addEdge(from, to, l, c);
        }

        Circulation circulation = new Circulation();
        circulation.findCirculation(graph, numVertices, numEdges);

        System.out.println();
    }

    private static void findCirculation(FlowGraph graph, int numVertices, int numEdges) {
        for (int vertex = 0; vertex < numVertices; vertex++) {
            if (graph.flowMap[vertex] < 0) {
                graph.addEdge(numVertices, vertex, 0, -graph.flowMap[vertex]);
            }

            if (graph.flowMap[vertex] > 0) {
                graph.addEdge(vertex, numVertices + 1, 0, graph.flowMap[vertex]);
                graph.flowResult += graph.flowMap[vertex];
            }
        }

        SearchResult result = maxFlow(graph, numVertices, numVertices + 1);

        if (!result.state){
            System.out.println("NO");
        }else{
            System.out.println("YES");
            for (int edge = 0; edge < numEdges; edge++) {
                Edge curr = graph.edges.get(edge*2);
                System.out.println(curr.flow + curr.l);
            }
        }
    }

    private static SearchResult maxFlow(FlowGraph graph, int from, int to) {
        int flow = 0;
        while (true) {
            List<Integer> path = new ArrayList<>();
            SearchResult result = search(graph, from, to, path);
            if (!result.state) {
                result.value = flow;
                if (flow == graph.flowResult){
                    result.state = true;
                }
                return result;
            }
            for (Integer edgeId : path) {
                graph.addFlow(result.value, edgeId);
            }
            flow += result.value;
        }
    }

    private static SearchResult search(FlowGraph graph, int from, int to, List<Integer> path) {
        int val = Integer.MAX_VALUE;
        boolean pathExists = false;
        int graphSize = graph.graph.length;

        int[] distance = new int[graphSize];
        Arrays.fill(distance, Integer.MAX_VALUE);
        ParentMap[] parent = new ParentMap[graphSize];

        Queue<Integer> qu = new LinkedList<Integer>();
        distance[from] = 0;
        qu.add(from);

        while (!qu.isEmpty()) {
            int curr = qu.remove();
            for (Integer edgeId : (List<Integer>) graph.graph[curr]) {
                Edge currEdge = graph.edges.get(edgeId);
                if (currEdge.cR > 0 && distance[currEdge.to] == Integer.MAX_VALUE) {
                    distance[currEdge.to] = distance[curr] + 1;
                    parent[currEdge.to] = new ParentMap(curr, edgeId);
                    qu.add(currEdge.to);
                    if (currEdge.to == to) {
                        SearchResult result = new SearchResult();
                        result.value = Integer.MAX_VALUE;
                        while (true){
                            path.add(0, edgeId);
                            result.value = Math.min(result.value, graph.edges.get(edgeId).cR);
                            if (curr == from){
                                break;
                            }
                            edgeId = parent[curr].edgeId;
                            curr = parent[curr].from;
                        }
                        result.state = true;
                        return result;
                    }
                }
            }

        }

        return new SearchResult();
    }

    private static class ParentMap {
        Integer from, edgeId;

        public ParentMap() {
            this.from = null;
            this.edgeId = null;
        }

        public ParentMap(Integer from, Integer edgeId) {
            this.from = from;
            this.edgeId = edgeId;
        }
    }

    private static class SearchResult {
        boolean state = false;
        int value;

        public SearchResult() {
        }

        public SearchResult(boolean state, int value) {
            this.state = state;
            this.value = value;
        }
    }
}
