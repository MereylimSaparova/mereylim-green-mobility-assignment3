"""Generate two required benchmark figures from the real Java CSV results."""
import csv
from pathlib import Path
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

root = Path(__file__).resolve().parent
with (root / 'results/tables/benchmarks.csv').open(newline='') as f:
    records = list(csv.DictReader(f))

for axis, filename, xlabel in [
    ('vertices', 'time_vs_vertices.png', 'Vertices (V)'),
    ('edges', 'time_vs_edges.png', 'Edges (E)'),
]:
    fig, ax = plt.subplots(figsize=(9, 5.5))
    for algorithm in ['Kruskal', 'Prim', 'Tarjan_SCC', 'Kahn_DAG', 'Dijkstra']:
        subset = sorted((r for r in records if r['algorithm'] == algorithm), key=lambda r: int(r[axis]))
        ax.plot([int(r[axis]) for r in subset], [float(r['average_time_ns']) / 1e6 for r in subset], marker='o', label=algorithm.replace('_', ' '))
    ax.set_xlabel(xlabel)
    ax.set_ylabel('Average execution time (milliseconds)')
    ax.set_title('Green Mobility Network: benchmark time vs. ' + axis)
    ax.grid(alpha=0.3)
    ax.legend()
    fig.tight_layout()
    fig.savefig(root / 'results/plots' / filename, dpi=180)
    plt.close(fig)
