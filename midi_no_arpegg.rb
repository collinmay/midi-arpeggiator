require "midilib"

$current_note = 0

def set_note(note)
  if note != $current_note then
    puts note
    $stdout.flush
  end
  $current_note = note
end

seq = MIDI::Sequence.new()
File.open(ARGV[0], "rb") do |f|
  seq.read(f)
end

Signal.trap("INT") do
  set_note(0)
  exit
end

ARPEGGIO_FREQ = 1.0

events = []

seq.each do |track|
  track.each do |event|
    events.push event
  end
end

events.sort_by do |event|
  event.time_from_start
end

notes = []
tgt = Time.now
pulse = 0
arpeggio = 0
note = 0

$stderr.puts "entering main event loop"

events.each do |event|
  tgt+= (seq.pulses_to_seconds(event.time_from_start - pulse))
  pulse = event.time_from_start
  
  while Time.now < tgt do
    sleep [Time.now - tgt, 0].max
  end
  
  if event.is_a? MIDI::NoteOn then
    set_note(event.note)
    note = event.note
  end
  
  if (event.is_a? MIDI::NoteOff) && (event.note == note) then
    set_note(0)
  end
end

set_note(0)
