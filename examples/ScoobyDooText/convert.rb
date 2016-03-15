HEAD = '<!DOCTYPE html>
<html>
<head>
  <link rel="stylesheet" href="style.css">
</head>
<body>'

TAIL = '</body>
</html>'

def process arg
	puts arg
	str1 = "And I would have gotten away with it if it wasn't for you meddling kids and your pesky dog..txt"
	str2 = "Start.txt"
	if arg == str1 || arg == str2
		return
	end

	f = File.open arg

	lines = f.readlines

	f.close

	out = File.open("../ScoobyDooHTML/#{arg.sub(".txt", ".html")}", "w")

	out.puts HEAD

	lines.each do |line|
		if line[0] == "#" || line[0] == '%'
			out.puts line
		else
			out.puts "<p>#{line.chomp}</p>"
		end
	end

	out.puts TAIL
end

ARGV.each do |i|
	process i
end
