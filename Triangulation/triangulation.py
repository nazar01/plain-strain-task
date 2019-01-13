import numpy as np
import math
# import matplotlib.pyplot as plt
import sys

MAIN_MATRIX = []

FIGURE_POINTS = []
RESULT_POINTS = []
RESULT_TRIAGLES=[]
	
# def draw_points(points,color):
# 	print(points)
# 	x = []
# 	y = []
# 	for point in points:
# 		x.append(point[0])
# 		y.append(point[1])
# 	x.append(points[0][0])
# 	y.append(points[0][1])
# 	plt.plot(x, y, color=color)
#
# def draw_all():
# 	for triangle in RESULT_TRIAGLES:
# 		draw_points(triangle, 'black')
# 	draw_points(FIGURE_POINTS, 'red')
# 	plt.show()
	
def get_vector(point_1, point_2):
	return point_2 - point_1
	
def get_e_vector(vector):
	return vector/np.linalg.norm(vector)
	
def get_line_coefs(point_1, point_2):
	return [point_1[1] - point_2[1],
				  point_2[0] - point_1[0],
				  point_1[0]*point_2[1] - point_1[1]*point_2[0]]
	
def calc_mid_e_vector(e_1, e_2):
	return (e_1+e_2)/2

def calc_mid_vector(mid_e_vector, length, point, coef):
	return np.round(mid_e_vector*length*coef + point,2)
	
def calc_angle(vector_1, vector_2):
	nom = np.round(np.dot(vector_1, vector_2),2)
	denom = np.round(np.linalg.norm(vector_1) * np.linalg.norm(vector_2), 2)
	return np.round(math.degrees(np.arccos(nom/denom)))
	
def find_min_angle(next = None):
	commonLen=len(FIGURE_POINTS)
	min_angle = 361
	for i in range(0, commonLen):
		start = FIGURE_POINTS[commonLen - 1 if (i == 0) else i - 1]
		mid = FIGURE_POINTS[i]
		end = FIGURE_POINTS[0 if (i == commonLen - 1) else i + 1]
		vector_1 = get_vector(np.array(start), np.array(mid))
		vector_2 = get_vector(np.array(mid), np.array(end))
		angle = 180 - calc_angle(vector_1, vector_2)
		print('CURRENT TRIANGLE: {} - {} - {}, VALUE: {}, INSIDE_POINT?: {}'.format(start, mid, end, angle, inside_point_vectors(vector_1, vector_2)))
		if (angle < min_angle) and (not inside_point_vectors(vector_1, vector_2)):
			min_angle = angle
			min_start_point = np.array(start)
			min_mid_point = np.array(mid)
			min_end_point = np.array(end)
	print('MIN TRIANGLE: {} - {} - {}'.format(min_start_point, min_mid_point, min_end_point))
	print('MIN ANGLE:', min_angle)
	return min_start_point, min_mid_point, min_end_point

def find_min_angles_list():
	commonLen=len(FIGURE_POINTS)
	angles_list = []
	for i in range(0, commonLen):
		start = FIGURE_POINTS[commonLen - 1 if (i == 0) else i - 1]
		mid = FIGURE_POINTS[i]
		end = FIGURE_POINTS[0 if (i == commonLen - 1) else i + 1]
		vector_1 = get_vector(np.array(start), np.array(mid))
		vector_2 = get_vector(np.array(mid), np.array(end))
		angle = 180 - calc_angle(vector_1, vector_2)
		print('CURRENT TRIANGLE: {} - {} - {}, VALUE: {}, INSIDE_POINT?: {}'.format(start, mid, end, angle, inside_point_vectors(vector_1, vector_2)))
		if (not inside_point_vectors(vector_1, vector_2)):
			angles_list.append([angle,[start, mid, end]])
	angles_list=sorted(angles_list, key=lambda x: x[0])
	print('Sorted angles list: {}'.format(angles_list))
	return angles_list
	
	
def make_recess(point_start, point_mid, point_end, coef):
	print('Its recess')
	vector_1 = get_vector(point_mid, point_start)
	vector_2 = get_vector(point_mid, point_end)
	e_vector_1 = get_e_vector(vector_1)
	e_vector_2 = get_e_vector(vector_2)
	mid_e_vector = calc_mid_e_vector(e_vector_1, e_vector_2)
	length = (np.linalg.norm(vector_1) + np.linalg.norm(vector_2))
	mid_vector = list(calc_mid_vector(mid_e_vector, length, point_mid, coef))
	min_distance = check_intersection(point_mid, mid_vector, length)
	mid_vector = list(calc_mid_vector(mid_e_vector, min_distance, point_mid, coef))
	
	RESULT_TRIAGLES.append([list(point_start), list(point_mid), list(mid_vector)])
	RESULT_TRIAGLES.append([list(mid_vector) ,list(point_mid), list(point_end)])
	
	point_mid = list(point_mid)
	index = FIGURE_POINTS.index(point_mid)
	FIGURE_POINTS.insert(index, mid_vector)
	RESULT_POINTS.append(mid_vector)
	FIGURE_POINTS.remove(point_mid)
	return True

	
def make_connect(point_start, point_mid, point_end, coef):
	print('its connect')
	temp_vector = get_vector(point_start, point_end)
	length = np.linalg.norm(temp_vector)
	min_distance = check_intersection(point_start, point_end, length)

	if min_distance == length:
		print('success')
		point_mid = list(point_mid)
		FIGURE_POINTS.remove(point_mid)
		RESULT_TRIAGLES.append([list(point_start) ,list(point_mid) ,list(point_end)])
		return True
	else:
		print('Not success')
		return False
	
def check_intersection(point_a, point_b, length):
	commonLen=len(FIGURE_POINTS)
	min_distance = length
	min_point = []
	for i in range(0, commonLen):
		point_c = FIGURE_POINTS[i]
		point_d = FIGURE_POINTS[0 if (i == commonLen - 1) else i + 1]
		if (list(point_c) != list(point_a)) and (list(point_d) != list(point_a) and list(point_c) != list(point_b)) and (list(point_d) != list(point_b)):
			A_1,B_1,C_1 = get_line_coefs(point_a, point_b)
			A_2,B_2,C_2 = get_line_coefs(point_c, point_d)
			M = [[A_1, B_1], [A_2, B_2]]
			Y = [-C_1, -C_2]
			try:
				print('vector: {} {}, side: {} {}'.format(point_a, point_b, point_c, point_d))
				solution = np.linalg.solve(M, Y)
				print('solution: {}'.format(solution))
				
				if inSquare(solution, point_c, point_d) and inSquare(solution, point_a, point_b): 
					vector_temp = get_vector(point_a, np.array(solution))
					if np.linalg.norm(vector_temp) < min_distance:
						min_distance = np.linalg.norm(vector_temp)
						min_point = solution
						print('intersection with {} - {}'.format(point_c, point_d))
				else: 
					print('no intersection in try\n')
			except:
				print('no intersection in exception\n')
				if(((point_b[0] - point_a[0])/(point_c[0] - point_a[0]) == (point_b[1] - point_a[1])/(point_c[1] - point_a[1])) and ((point_b[0] - point_a[0])/(point_d[0] - point_a[0]) == (point_b[1] - point_a[1])/(point_d[1] - point_a[1]))):
					min_distance=0
	if(len(min_point) == 2):
		print('MIN DIST INTERSECTION WITH: {} - {}, VALUE: {}'.format(min_point[0], min_point[1], min_distance))
	else:
		print('NO INTERSECTIONS')              
	return min_distance

def inSquare(point, start, end):
	epsilon = 0.000000000000001
	minX=min(start[0], end[0])
	maxX=max(start[0], end[0])
	minY=min(start[1], end[1])
	maxY=max(start[1], end[1])
	return (point[0] >= minX or abs(point[0] - minX) < epsilon)\
 and (point[0] <= maxX or abs(point[0] - maxX) < epsilon)\
 and (point[1] >= minY or abs(point[1] - minY) < epsilon)\
 and (point[1] <= maxY or abs(point[1] - maxY) < epsilon) 
				

def inside_point_vectors(a,b):
	return a[0]*b[1] - a[1]*b[0] < 0
	
def solve_triangle(point_start, point_mid, point_end, coef):
	#add if condition to check points
	
	vector_mid_start = get_vector(point_mid, point_start)
	vector_start_mid = get_vector(point_start, point_mid)
	
	vector_mid_end = get_vector(point_mid, point_end)
	vector_end_mid = get_vector(point_end, point_mid)
	
	vector_start_end = get_vector(point_start, point_end)
	vector_end_start = get_vector(point_end, point_start)
	
	angle_start = calc_angle(vector_start_end, vector_start_mid)
	angle_mid = calc_angle(vector_mid_start, vector_mid_end)
	angle_end = calc_angle(vector_end_mid, vector_end_start)
	
	success = False
	if (angle_mid <= 75) or ((angle_mid > 75) and (angle_mid < 90) and (angle_start > 30) and (angle_end > 30)):
		success = make_connect(point_start, point_mid, point_end, coef)
	elif (angle_mid >= 89) or ((angle_start <= 30) and (angle_end <= 30)):
		success = make_recess(point_start, point_mid, point_end, coef)
	else:
		success = make_connect(point_start, point_mid, point_end, coef)
	return  success
		
def solve_figure(coef):
	iteration = 0
	while ( len(FIGURE_POINTS) > 3):
		print('FIGURE_POINTS: {}'.format(FIGURE_POINTS))
		print('RESULT_TRIANGLES: {}'.format(RESULT_TRIAGLES))
		iteration += 1
		print('iteration: {}'.format(iteration))
		sorted_angles_list = find_min_angles_list()
		for angle_info in sorted_angles_list:
			print('Try process angle: {}'.format(angle_info))
			if solve_triangle(np.array(angle_info[1][0]), np.array(angle_info[1][1]), np.array(angle_info[1][2]), coef):
				break
		#draw_all()
	RESULT_TRIAGLES.append([FIGURE_POINTS[0], FIGURE_POINTS[1], FIGURE_POINTS[2]])
	# draw_all()


def read_data_from(filename):
	file = open(filename, 'r')
	points=[]
	for line in file.readlines():
		split = line.split(sep=' ')
		points.append([float(split[0]),float(split[1])])
	print('input points: {}'.format(points))
	file.close()
	return points


def write_result(filename, array_of_strings):
	file = open(filename, 'w')
	file.write('\n'.join(array_of_strings))
	file.close()
	
coef = 1

#DONE
#FIGURE_POINTS = [[2,0],  [3,5], [2,10],  [1,5]]
#FIGURE_POINTS = list(reversed([[1,10], [2,15], [3.5,12.5], [5,15], [6,10], [4,7.5], [6,5], [5,0], [3.5, 5], [2,0], [1,5], [3,7.5]]))
#FIGURE_POINTS = list(reversed([[2,1], [1,5], [2,10], [3,5.5], [4,10], [5,5], [4,1], [3,4.5]]))
#FIGURE_POINTS = list(reversed([[1,1], [1,5], [1,10], [3, 10], [5,10], [7,10], [10,10], [10,7], [10,5], [10,3], [10,1]]))

#FIGURE_POINTS = list(reversed([[1,1], [1,10], [10,10], [10,7], [3, 5], [10,3], [10,1]]))
#FIGURE_POINTS = list(reversed([[5,5], [5,10], [7,10], [7,5]]))
#FIGURE_POINTS = list(reversed([[1,10], [2,15], [5,15], [6,10], [6,5], [5,0], [2,0], [1,5]]))
#FIGURE_POINTS = list(reversed([[1,1], [1,5], [5,5], [5,10], [7,10], [7,5], [10,5], [10,1]]))
#write_result('fig7.txt', list(map(lambda point: '{} {}'.format(point[0], point[1]),FIGURE_POINTS)))

	

print('START')
# FIGURE_POINTS=read_data_from(sys.argv[1])
FIGURE_POINTS=read_data_from('../src_points.txt')
# draw_all()
RESULT_POINTS = FIGURE_POINTS.copy()
solve_figure(coef)
all_points = list(map(lambda point: '{} {}'.format(point[0], point[1]), RESULT_POINTS))
all_triangles = list(map(lambda triangle: '{} {} {}'.format(\
						  RESULT_POINTS.index(triangle[0]),\
						  RESULT_POINTS.index(triangle[1]),\
						  RESULT_POINTS.index(triangle[2])), RESULT_TRIAGLES))
write_result('../points.txt', all_points)
write_result('../triangles.txt', all_triangles)

print('OUTPUT POINTS: ', RESULT_POINTS)
print('OUTPUT TRIANGLES: ', RESULT_TRIAGLES)

print('END')
