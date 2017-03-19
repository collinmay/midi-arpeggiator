require "midilib"


def set_note(note)
  puts note.to_s
  $stdout.flush
end

seq = MIDI::Sequence.new()
File.open(ARGV[0], "rb") do |f|
  seq.read(f)
end

Signal.trap("INT") do
  set_note(0)
  exit
end

ARPEGGIO_FREQ = 80.0

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
  $stderr.puts "increment"
  tgt+= seq.pulses_to_seconds(event.time_from_start - pulse)
  pulse = event.time_from_start
  
  while Time.now < tgt do
    $stderr.puts "t-tgt: " + (Time.now - tgt).to_s
    sleep 1.0/ARPEGGIO_FREQ
    
    if notes.length > 0 then
      if note != notes[arpeggio % notes.length] then
        set_note(notes[arpeggio % notes.length])
        note = notes[arpeggio % notes.length]
      end
    else
      note = 0
      set_note(0)
    end
    
    arpeggio+= 1
  end
  
  if event.is_a? MIDI::NoteOn then
    if event.velocity > 100 then
      notes.push(event.note)
    end
  end
  
  if event.is_a? MIDI::NoteOff then
    notes.delete(event.note)
  end
end

set_note(0)
