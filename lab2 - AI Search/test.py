import networkx as nx
import matplotlib.pyplot as plt

# Create a 4x4 grid graph
grid_size = 4
G = nx.grid_2d_graph(grid_size, grid_size)

# Create a mapping for positions to node labels
pos = {(x, y): (y, -x) for x, y in G.nodes()}

# Draw the graph
plt.figure(figsize=(8, 8))
nx.draw(G, pos, with_labels=True, node_size=700, node_color='lightblue', font_size=10, font_color='black')
plt.title("Tree Graph for a 4x4 Grid")
plt.grid(False)
plt.show()
